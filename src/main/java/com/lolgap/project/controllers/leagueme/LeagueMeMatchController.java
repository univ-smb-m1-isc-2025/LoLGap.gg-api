package com.lolgap.project.controllers.leagueme;

import com.lolgap.project.dto.MatchDetailsDTO;
import com.lolgap.project.services.LeagueMatch;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/league/me/matches")
@RequiredArgsConstructor
public class LeagueMeMatchController {
    private final LeagueMatch leagueMatch;

    @GetMapping()
    public ResponseEntity<?> getMatchHistory() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            List<String> matches = leagueMatch.ofUsername(auth.getName()).get();
            return ResponseEntity.ok(matches);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/details")
    public ResponseEntity<?> getMatchHistoryWithDetails(@RequestParam(defaultValue = "5") int count) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            List<MatchDetailsDTO> matches = leagueMatch.ofUsernameWithDetails(auth.getName(), count).get();
            return ResponseEntity.ok("matches");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{matchId}")
    public ResponseEntity<?> getMatchDetails(@PathVariable String matchId) {
        try {
            MatchDetailsDTO match = leagueMatch.detailsOfMatch(matchId).get();
            return ResponseEntity.ok(match);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/queue/{queueId}")
    public ResponseEntity<?> getMatchHistoryByQueue(@PathVariable int queueId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            List<String> matches = leagueMatch.ofUsernameByQueue(auth.getName(), queueId).get();
            return ResponseEntity.ok(matches);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/queue/{queueId}/details")
    public ResponseEntity<?> getMatchHistoryWithDetailsByQueue(
            @PathVariable int queueId,
            @RequestParam(defaultValue = "5") int count) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            List<MatchDetailsDTO> matches = leagueMatch.ofUsernameWithDetailsByQueue(auth.getName(), queueId, count).get();
            return ResponseEntity.ok(matches);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
} 