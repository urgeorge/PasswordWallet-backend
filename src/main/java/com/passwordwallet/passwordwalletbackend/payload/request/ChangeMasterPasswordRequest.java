package com.passwordwallet.passwordwalletbackend.payload.request;

import com.passwordwallet.passwordwalletbackend.models.PasswordProtection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChangeMasterPasswordRequest {

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private PasswordProtection passwordProtection;

}
