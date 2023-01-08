package com.passwordwallet.passwordwalletbackend.security.models;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Table(name = "users")
public class LastLoginInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Timestamp lastSuccessAttempt;

    private Timestamp lastFailAttempt;

    @NotNull
    private Long userId;
}
