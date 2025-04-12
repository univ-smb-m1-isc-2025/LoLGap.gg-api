package com.lolgap.project.controllers.league;

import com.lolgap.project.dto.MatchDetailsDTO;
import com.lolgap.project.models.Account;
import com.lolgap.project.repositories.AccountRepository;
import com.lolgap.project.services.LeagueMatch;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/league/users/{userId}/matches")
@RequiredArgsConstructor
public class LeagueMatchController {
    private final LeagueMatch leagueMatch;
    private final AccountRepository accountRepository;

    @GetMapping()
    public ResponseEntity<?> getMatchHistory(@PathVariable Long userId) {
        try {
            Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (account.getRiotPuuid() == null || account.getRiotPuuid().isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<String> matchIds = leagueMatch.ofPuuid(account.getRiotPuuid()).get();
            return ResponseEntity.ok(matchIds);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/details")
    public ResponseEntity<?> getMatchHistoryWithDetails(@PathVariable Long userId, @RequestParam(defaultValue = "5") int count) {
        try {
            Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (account.getRiotPuuid() == null || account.getRiotPuuid().isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<MatchDetailsDTO> matchDetails = leagueMatch.ofPuuidWithDetails(account.getRiotPuuid(), count).get();
            return ResponseEntity.ok(matchDetails);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{matchId}")
    public ResponseEntity<?> getMatchDetails(@PathVariable Long userId, @PathVariable String matchId) {
        try {
            Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (account.getRiotPuuid() == null || account.getRiotPuuid().isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            MatchDetailsDTO matchDetails = leagueMatch.detailsOfMatch(matchId).get();
            return ResponseEntity.ok(matchDetails);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
} 