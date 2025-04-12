package com.lolgap.project.controllers.leagueme;

import com.lolgap.project.dto.LeagueEntryDTO;
import com.lolgap.project.services.LeagueRank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/league/me/ranks")
@RequiredArgsConstructor
public class LeagueMeRankController
{
    private final LeagueRank leagueRank;

    @GetMapping()
    public ResponseEntity<?> getMyRanks()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try
        {
            List<LeagueEntryDTO> ranks = leagueRank.ofUsername(auth.getName()).get();
            return ResponseEntity.ok(ranks);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.notFound().build();
        }
    }
} 