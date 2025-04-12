package com.lolgap.project.controllers.riot;

import com.lolgap.project.dto.RiotAccountDTO;
import com.lolgap.project.models.Account;
import com.lolgap.project.repositories.AccountRepository;
import com.lolgap.project.services.RiotAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/riot/users/{userId}")
@RequiredArgsConstructor
public class RiotAccountController {
    private final RiotAccount riotAccount;
    private final AccountRepository accountRepository;

    @GetMapping()
    public ResponseEntity<?> getRiotAccount(@PathVariable Long userId) {
        try {
            Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (account.getRiotGameName() == null || account.getRiotTagLine() == null) {
                return ResponseEntity.notFound().build();
            }

            RiotAccountDTO riotAccountDTO = riotAccount.ofRiotId(account.getRiotGameName(), account.getRiotTagLine()).get();
            return ResponseEntity.ok(riotAccountDTO);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
} 