package com.lolgap.project.controllers.league;

import com.lolgap.project.dto.LeagueEntryDTO;
import com.lolgap.project.models.Account;
import com.lolgap.project.repositories.AccountRepository;
import com.lolgap.project.services.LeagueRank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/league/users/{userId}/ranks")
@RequiredArgsConstructor
public class LeagueRankController {
    private final LeagueRank leagueRank;
    private final AccountRepository accountRepository;

    @GetMapping()
    public ResponseEntity<?> getLeagueRank(@PathVariable Long userId) {
        try {
            Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (account.getRiotPuuid() == null || account.getRiotPuuid().isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<LeagueEntryDTO> leagueEntries = leagueRank.ofPuuid(account.getRiotPuuid()).get();
            return ResponseEntity.ok(leagueEntries);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
} 