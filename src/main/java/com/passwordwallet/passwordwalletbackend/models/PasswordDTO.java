package com.passwordwallet.passwordwalletbackend.models;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Builder
public class PasswordDTO {

    Long id;
    String websiteName;
    String password;
    String description;
    String createdAt;
    String updatedAt;
}
