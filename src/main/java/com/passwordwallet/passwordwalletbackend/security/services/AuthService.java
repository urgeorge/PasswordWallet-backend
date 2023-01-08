package com.passwordwallet.passwordwalletbackend.security.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

   public Optional<UserDetailsImpl> getLoggedUserDetails(){

       return SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
               SecurityContextHolder.getContext().getAuthentication().getPrincipal()!="anonymousUser" ?
               Optional.of((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()) :
               Optional.empty();
    }

    public void saveLoginAttempt(LocalDateTime now, boolean authenticated, String remoteAddr) {
    }
}
