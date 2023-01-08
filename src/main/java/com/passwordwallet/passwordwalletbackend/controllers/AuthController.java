package com.passwordwallet.passwordwalletbackend.controllers;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.passwordwallet.passwordwalletbackend.models.PasswordProtection;
import com.passwordwallet.passwordwalletbackend.payload.request.ChangeMasterPasswordRequest;
import com.passwordwallet.passwordwalletbackend.repository.RoleRepository;
import com.passwordwallet.passwordwalletbackend.repository.UserRepository;
import com.passwordwallet.passwordwalletbackend.security.services.AuthService;
import com.passwordwallet.passwordwalletbackend.security.services.CustomPasswordEncoder;
import com.passwordwallet.passwordwalletbackend.security.services.LoginAttemptService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passwordwallet.passwordwalletbackend.models.role.ERole;
import com.passwordwallet.passwordwalletbackend.models.role.Role;
import com.passwordwallet.passwordwalletbackend.models.User;
import com.passwordwallet.passwordwalletbackend.payload.request.LoginRequest;
import com.passwordwallet.passwordwalletbackend.payload.request.SignupRequest;
import com.passwordwallet.passwordwalletbackend.payload.response.JwtResponse;
import com.passwordwallet.passwordwalletbackend.payload.response.MessageResponse;
import com.passwordwallet.passwordwalletbackend.security.jwt.JwtUtils;
import com.passwordwallet.passwordwalletbackend.security.services.UserDetailsImpl;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    CustomPasswordEncoder customPasswordEncoder;

    @Autowired
    AuthService authService;

    @Autowired
    LoginAttemptService loginAttemptService;


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        //TODO: Refactoring - wydzielić fasadę do autoryzacji i przenieść kod z kontrolera

        if(loginAttemptService.isIpBlocked(request.getRemoteAddr())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Too many login attempts");
        }

        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow(() -> new IllegalArgumentException("User not found"));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), user.getSalt()+loginRequest.getPassword()));

        //Zmiany
        this.loginAttemptService.recordLoginAttempt(user.getId(), request.getRemoteAddr(), authentication.isAuthenticated());

        //
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());



        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        String salt = RandomStringUtils.randomAlphanumeric(20);
        User user;

        if(signUpRequest.getPasswordProtection().equals(PasswordProtection.SHA512)){
            user = new User(
                    signUpRequest.getUsername(),
                    signUpRequest.getEmail(),
                    customPasswordEncoder.calculateSHA512(salt+signUpRequest.getPassword()),
                    salt);
        }
        else {
            user = new User(signUpRequest.getUsername(),
                    signUpRequest.getEmail(),
                    customPasswordEncoder.calculateHMAC(signUpRequest.getPassword()),
                    "");
        }

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/changeMasterPassword")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> changeMasterPassword(@Valid @RequestBody ChangeMasterPasswordRequest changeMasterPasswordRequest) {

        User user = userRepository.findById(authService.getLoggedUserDetails()
                        .orElseThrow(() -> new IllegalStateException("User not authenticated"))
                        .getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String newSalt = RandomStringUtils.randomAlphanumeric(20);

        user.setPassword(changeMasterPasswordRequest.getPasswordProtection().equals(PasswordProtection.SHA512) ?
                customPasswordEncoder.calculateSHA512(newSalt+changeMasterPasswordRequest.getPassword()) :
                customPasswordEncoder.calculateHMAC(changeMasterPasswordRequest.getPassword()));
        user.setSalt(changeMasterPasswordRequest.getPasswordProtection().equals(PasswordProtection.SHA512) ? newSalt : "");
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Master password changed successfully!"));
    }

//    @GetMapping("/loginInfo")
//    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
//    public ResponseEntity<?> getLoginInfo() {
//    }
}
