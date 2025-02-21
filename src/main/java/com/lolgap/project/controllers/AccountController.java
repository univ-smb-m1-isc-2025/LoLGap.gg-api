package com.lolgap.project.controllers;

import com.lolgap.project.models.Account;
import com.lolgap.project.repositories.AccountRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.lolgap.project.security.JwtUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;  // Ajouter cet import
import java.util.List;
import org.springframework.web.bind.annotation.*;


@Controller
public class AccountController {
    private final AccountRepository accountRepository;

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping("/accounts")
    @ResponseBody
    public List<Account> listAll() {
        return accountRepository.findAll(); 
    }

    @PostMapping("/accounts")
    @ResponseBody
    public Account createAccount(@RequestBody Account newAccount) {
        if (newAccount.getUsername() == null || newAccount.getPassword() == null) {
            throw new IllegalArgumentException("Username and password must not be null");
        }
        return accountRepository.save(newAccount);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Account account) {
        Account existingAccount = accountRepository.findByUsername(account.getUsername());
        JwtUtil jwtUtil = new JwtUtil();
        if (existingAccount != null && existingAccount.getPassword().equals(account.getPassword())) {
            String token = jwtUtil.generateToken(existingAccount.getUsername());
            return ResponseEntity.ok().body("Bearer " + token);
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

}