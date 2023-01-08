package com.passwordwallet.passwordwalletbackend.repository;

import com.passwordwallet.passwordwalletbackend.models.User;
import com.passwordwallet.passwordwalletbackend.security.models.LastLoginInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface LastLoginInfoRepository extends JpaRepository<LastLoginInfo, Long> {
    Optional<LastLoginInfo> findByUserId(Long userId);

}
