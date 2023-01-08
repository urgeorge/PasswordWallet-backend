package com.passwordwallet.passwordwalletbackend.repository;

import java.util.Optional;

import com.passwordwallet.passwordwalletbackend.models.role.ERole;
import com.passwordwallet.passwordwalletbackend.models.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
