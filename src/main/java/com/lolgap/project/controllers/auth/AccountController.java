package com.lolgap.project.controllers.auth;

import com.lolgap.project.models.Account;
import com.lolgap.project.repositories.AccountRepository;
import com.lolgap.project.security.JwtUtil;
import com.lolgap.project.services.RiotAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class AccountController
{

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RiotAccount riotAccount;


    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<?> createAccount(@RequestBody Account newAccount)
    {
        if (newAccount.getUsername() == null || newAccount.getPassword() == null)
        {
            return ResponseEntity.badRequest().body("Username and password must not be null");
        }
        
        if (accountRepository.findByUsername(newAccount.getUsername()) != null)
        {
            return ResponseEntity.badRequest().body("Username already taken HAHA");
        }

        if (newAccount.getRiotGameName() == null || newAccount.getRiotTagLine() == null)
        {
            return ResponseEntity.badRequest().body("Riot game name and tag line must not be null");
        }
        
        try
        {
            newAccount = riotAccount.enrich(newAccount).join();
            newAccount.setPassword(passwordEncoder.encode(newAccount.getPassword()));
            return ResponseEntity.ok(accountRepository.save(newAccount));
        } catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody Account account)
    {
        try
        {
            authenticationManager.authenticate
            (
                    new UsernamePasswordAuthenticationToken(account.getUsername(), account.getPassword())
            );
            String token = jwtUtil.generateToken(account.getUsername());
            Account userAccount = accountRepository.findByUsername(account.getUsername());
            
            Map<String, Object> response = new HashMap<>();
            response.put("user", userAccount);
            response.put("token", token);
            
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e)
        {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @GetMapping("/me")
    @ResponseBody
    public ResponseEntity<?> listAll()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        try {
            Account account = accountRepository.findByUsername(username);
            
            if (account == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
