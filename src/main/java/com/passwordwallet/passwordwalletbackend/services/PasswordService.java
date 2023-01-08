package com.passwordwallet.passwordwalletbackend.services;

import com.passwordwallet.passwordwalletbackend.models.Password;
import com.passwordwallet.passwordwalletbackend.models.PasswordDTO;
import com.passwordwallet.passwordwalletbackend.models.User;
import com.passwordwallet.passwordwalletbackend.repository.PasswordRepository;
import com.passwordwallet.passwordwalletbackend.repository.UserRepository;
import com.passwordwallet.passwordwalletbackend.security.services.AuthService;
import com.passwordwallet.passwordwalletbackend.security.services.UserDetailsImpl;
import com.passwordwallet.passwordwalletbackend.security.utils.AESenc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PasswordService {

    private static final String ALGORITHM = "AES";

    private final PasswordRepository passwordRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Autowired
    PasswordService(PasswordRepository passwordRepository, UserRepository userRepository, AuthService authService){
        this.passwordRepository = passwordRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public List<PasswordDTO> findAllUserPasswords() {
        return passwordRepository.findAllByUserId(authService.getLoggedUserDetails()
                        .orElseThrow(() -> new IllegalStateException("User not authenticated"))
                        .getId())
                .stream()
                .map(this::mapToPasswordDto)
                .collect(Collectors.toList());
    }

    public void addPassword(PasswordDTO password) throws Exception {
        passwordRepository.save(mapToPassword(password));
    }

    public void updatePassword(PasswordDTO password) throws Exception {
        passwordRepository.findById(password
                .getId())
                .orElseThrow(() -> new IllegalArgumentException("No password found"));

        passwordRepository.save(mapToPassword(password));
    }

    public void deletePassword(Long passwordId) {
        Password password = passwordRepository.findById(passwordId).orElseThrow(() -> new IllegalArgumentException("Invalid password id"));
        passwordRepository.delete(password);
    }


    public PasswordDTO getDecryptedPassword(Long passwordId) throws Exception {
        String hashedMasterPassword = authService.getLoggedUserDetails().orElseThrow(() -> new IllegalStateException("Unauthorized operation")).getPassword();
        Password passwordModel = passwordRepository.findById(passwordId).orElseThrow(() -> new IllegalArgumentException("Invalid password id"));
        String truePassword = passwordModel.getPassword();
        passwordModel.setPassword(AESenc.decrypt(truePassword, new SecretKeySpec(Arrays.copyOfRange(
                        hashedMasterPassword.getBytes(),
                        hashedMasterPassword.getBytes().length - 32,
                        hashedMasterPassword.getBytes().length),
                        ALGORITHM)));
        return mapToPasswordDto(passwordModel);

    }

    private PasswordDTO mapToPasswordDto(Password password) {
        return PasswordDTO.builder()
                .id(password.getId())
                .websiteName(password.getWebsiteName())
                .password(password.getPassword())
                .description(password.getDescription())
                .createdAt(password.getCreatedAt().toString())
                .updatedAt(password.getUpdatedAt().toString())
                .build();
    }

    private Password mapToPassword(PasswordDTO passwordDto) throws Exception {
        UserDetailsImpl user = authService.getLoggedUserDetails().orElseThrow(() -> new IllegalStateException("User not authenticated"));

        return Password.builder()
                .id(passwordDto.getId())
                .websiteName(passwordDto.getWebsiteName())
                .password(AESenc.encrypt(
                        passwordDto.getPassword(),
                        new SecretKeySpec(Arrays.copyOfRange(
                                user.getPassword().getBytes(),
                                user.getPassword().getBytes().length-32,
                                user.getPassword().getBytes().length),
                                ALGORITHM)))
                .description(passwordDto.getDescription())
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
                .userId(user.getId())
                .build();
    }

}
