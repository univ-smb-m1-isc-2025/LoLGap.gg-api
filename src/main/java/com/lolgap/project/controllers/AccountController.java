package com.lolgap.project.controllers;

import com.lolgap.project.models.Account;
import com.lolgap.project.repositories.AccountRepository;
import com.lolgap.project.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @GetMapping
    @ResponseBody
    public List<Account> listAll() {
        return accountRepository.findAll();
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<?> createAccount(@RequestBody Account newAccount) {
        if (newAccount.getUsername() == null || newAccount.getPassword() == null) {
            return ResponseEntity.badRequest().body("Username and password must not be null");
        }
        if (accountRepository.findByUsername(newAccount.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Username already taken");
        }
        newAccount.setPassword(passwordEncoder.encode(newAccount.getPassword()));
        return ResponseEntity.ok(accountRepository.save(newAccount));
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody Account account) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(account.getUsername(), account.getPassword())
            );
            String token = jwtUtil.generateToken(account.getUsername());
            return ResponseEntity.ok().body("Bearer " + token);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
