package com.lolgap.project.controllers.auth;

import com.lolgap.project.models.Account;
import com.lolgap.project.repositories.AccountRepository;
import com.lolgap.project.security.JwtUtil;
import com.lolgap.project.services.RiotAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    private final RiotAccountService riotAccountService;

    @GetMapping
    @ResponseBody
    public List<Account> listAll()
    {
        return accountRepository.findAll();
    }

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
            return ResponseEntity.badRequest().body("Username already taken");
        }

        if (newAccount.getRiotGameName() == null || newAccount.getRiotTagLine() == null)
        {
            return ResponseEntity.badRequest().body("Riot game name and tag line must not be null");
        }
        
        try
        {
            newAccount = riotAccountService.enrichAccountWithRiotInfo(newAccount);
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
}
