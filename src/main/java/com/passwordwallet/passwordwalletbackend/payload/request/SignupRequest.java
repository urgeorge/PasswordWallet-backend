package com.passwordwallet.passwordwalletbackend.payload.request;

import com.passwordwallet.passwordwalletbackend.models.PasswordProtection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Set;

import javax.validation.constraints.*;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private Set<String> role;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private PasswordProtection passwordProtection;

}
