package com.lolgap.project.controllers.league;

import com.lolgap.project.dto.LeagueAccountDTO;
import com.lolgap.project.models.Account;
import com.lolgap.project.repositories.AccountRepository;
import com.lolgap.project.services.LeagueAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/league/users/{userId}")
@RequiredArgsConstructor
public class LeagueAccountController {
    private final LeagueAccount leagueAccount;
    private final AccountRepository accountRepository;

    @GetMapping()
    public ResponseEntity<?> getLeagueAccount(@PathVariable Long userId) {
        try {
            Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (account.getRiotPuuid() == null || account.getRiotPuuid().isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            LeagueAccountDTO leagueAccountDTO = leagueAccount.ofPuuid(account.getRiotPuuid()).get();
            return ResponseEntity.ok(leagueAccountDTO);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
} 