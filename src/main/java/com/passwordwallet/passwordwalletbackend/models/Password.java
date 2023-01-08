package com.passwordwallet.passwordwalletbackend.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Entity
@Table(name = "passwords")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Password {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank
    @Size(max = 50)
    private String websiteName;

    @NotBlank
    @Size(max = 256)
    private String password;

    @Size(max = 256)
    private String description;

    @NotNull
    private Timestamp createdAt;

    @NotNull
    private Timestamp updatedAt;

    private Long userId;
}
