package com.lolgap.project.controllers.leagueme;

import com.lolgap.project.dto.RiotAccountDTO;
import com.lolgap.project.dto.LeagueAccountDTO;
import com.lolgap.project.dto.LeagueEntryDTO;
import com.lolgap.project.models.Account;
import com.lolgap.project.repositories.AccountRepository;
import com.lolgap.project.services.RiotAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/league/me")
@RequiredArgsConstructor
public class LeagueMeAccountController {
    private final RiotAccountService riotAccountService;
    private final AccountRepository accountRepository;

    @GetMapping("/")
    public ResponseEntity<?> getLeagueAccount()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userID = authentication.getPrincipal().toString();
        Account account = accountRepository.findById(Long.parseLong(userID))
                .orElse(null);
        
        if (account == null)
        {
            return ResponseEntity.notFound().build();
        }

        LeagueAccountDTO leagueAccount = riotAccountService.getLeagueAccountByPuuid(account.getRiotPuuid());  
        
        if (leagueAccount == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(leagueAccount);
    }   
}
