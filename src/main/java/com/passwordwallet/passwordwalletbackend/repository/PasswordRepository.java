package com.passwordwallet.passwordwalletbackend.repository;

import com.passwordwallet.passwordwalletbackend.models.Password;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PasswordRepository extends JpaRepository<Password,Long> {
    List<Password> findAllByUserId(Long userId);

    Optional<Password> findByWebsiteName(String websiteName);
}
