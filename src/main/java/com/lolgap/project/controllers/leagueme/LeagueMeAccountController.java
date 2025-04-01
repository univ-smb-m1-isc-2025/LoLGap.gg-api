package com.lolgap.project.controllers.leagueme;

import com.lolgap.project.dto.LeagueAccountDTO;
import com.lolgap.project.services.LeagueAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/league/me")
@RequiredArgsConstructor
public class LeagueMeAccountController
{
    private final LeagueAccount leagueAccount;

    @GetMapping()
    public ResponseEntity<?> getLeagueAccount()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        try {
            LeagueAccountDTO leagueAccountDTO = leagueAccount.ofUsername(username)
                .get();
            
            if (leagueAccountDTO == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(leagueAccountDTO);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }   
}
