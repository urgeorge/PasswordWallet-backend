package com.passwordwallet.passwordwalletbackend.controllers;


import com.passwordwallet.passwordwalletbackend.models.PasswordDTO;
import com.passwordwallet.passwordwalletbackend.services.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/password")
public class PasswordController {

    PasswordService passwordService;

    @Autowired
    PasswordController(PasswordService passwordService){
        this.passwordService = passwordService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<PasswordDTO> findAllUserPasswords() {
        return passwordService.findAllUserPasswords();
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public void addPassword(@Valid @RequestBody PasswordDTO passwordDto) throws Exception {
        passwordService.addPassword(passwordDto);
    }

    @PutMapping
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public void editPassword(@Valid @RequestBody PasswordDTO passwordDto) throws Exception {
        passwordService.updatePassword(passwordDto);
    }

    @DeleteMapping("{passwordId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public void deletePassword(@PathVariable Long passwordId) {
        passwordService.deletePassword(passwordId);
    }

    @GetMapping("decrypted/{passwordId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public PasswordDTO getDecryptedPassword(@PathVariable Long passwordId) throws Exception {
        return passwordService.getDecryptedPassword(passwordId);
    }
}
