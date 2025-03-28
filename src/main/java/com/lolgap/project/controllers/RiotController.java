package com.lolgap.project.controllers;

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
@RequestMapping("/api/riot")
@RequiredArgsConstructor
public class RiotController
{
    private final RiotAccountService riotAccountService;
    private final AccountRepository accountRepository;

    @GetMapping("/account")
    public ResponseEntity<?> getCurrentUserRiotAccount()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Account account = accountRepository.findByUsername(username);
        if (account == null)
        {
            return ResponseEntity.notFound().build();
        }

        RiotAccountDTO accountInfo = riotAccountService.getRiotAccountInfo(
            account.getRiotGameName(), 
            account.getRiotTagLine()
        );
        
        if (accountInfo == null)
        {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(accountInfo);
    }

    @GetMapping("/league")
    @ResponseBody
    public ResponseEntity<?> getCurrentUserLeagueAccount()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();
        Account account = accountRepository.findByUsername(username);
        if (account == null)
        {
            return ResponseEntity.status(404).body("Compte utilisateur non trouvé");
        }

        if (account.getRiotPuuid() == null || account.getRiotPuuid().isEmpty())
        {
            return ResponseEntity.status(404).body("Informations Riot non trouvées pour ce compte");
        }

        try
        {
            LeagueAccountDTO leagueInfo = riotAccountService.getLeagueAccountByPuuid(account.getRiotPuuid());
            if (leagueInfo == null)
            {
                return ResponseEntity.status(404).body("Compte League of Legends non trouvé");
            }
            return ResponseEntity.ok(leagueInfo);
        } catch (IllegalArgumentException e)
        {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e)
        {
            return ResponseEntity.status(500).body("Erreur serveur : " + e.getMessage());
        }
    }

    @GetMapping("getRanks")
    @ResponseBody
    public ResponseEntity<?> getRanks()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Account account = accountRepository.findByUsername(username);
        if (account == null)
        {
            return ResponseEntity.status(404).body("Compte utilisateur non trouvé");
        }

        if (account.getRiotPuuid() == null || account.getRiotPuuid().isEmpty())
        {
            return ResponseEntity.status(404).body("Informations Riot non trouvées pour ce compte");
        }

        List<LeagueEntryDTO> ranks = riotAccountService.getRanks(account.getRiotPuuid());
        if (ranks == null || ranks.isEmpty()) {
            return ResponseEntity.status(404).body("Aucun rang trouvé pour ce compte");
        }
        return ResponseEntity.ok(ranks);
    }

    @GetMapping("getMatchHistory/{queueId}")
    @ResponseBody       
    public ResponseEntity<?> getMatchHistory(
        @PathVariable int queueId)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Account account = accountRepository.findByUsername(username);
        if (account == null)
        {
            return ResponseEntity.status(404).body("Compte utilisateur non trouvé");
        }

        if (account.getRiotPuuid() == null || account.getRiotPuuid().isEmpty())
        {
            return ResponseEntity.status(404).body("Informations Riot non trouvées pour ce compte");
        }

        List<String> matchIds = riotAccountService.getMatchHistory(account.getRiotPuuid(), queueId);
        if (matchIds == null || matchIds.isEmpty()) {
            return ResponseEntity.status(404).body("Aucun match trouvé pour ce compte");
        }
        return ResponseEntity.ok(matchIds);
    }

    @GetMapping("getMatchHistory")
    @ResponseBody       
    public ResponseEntity<?> getMatchHistory()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Account account = accountRepository.findByUsername(username);
        if (account == null)
        {
            return ResponseEntity.status(404).body("Compte utilisateur non trouvé");
        }

        if (account.getRiotPuuid() == null || account.getRiotPuuid().isEmpty())
        {
            return ResponseEntity.status(404).body("Informations Riot non trouvées pour ce compte");
        }

        List<String> matchIds = riotAccountService.getMatchHistory(account.getRiotPuuid());
        if (matchIds == null || matchIds.isEmpty()) {
            return ResponseEntity.status(404).body("Aucun match trouvé pour ce compte");
        }
        return ResponseEntity.ok(matchIds);
    }

    @GetMapping("getMatchDetails/{matchId}")
    @ResponseBody
    public ResponseEntity<?> getMatchDetails(@PathVariable String matchId)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Account account = accountRepository.findByUsername(username);
        if (account == null)
        {
            return ResponseEntity.status(404).body("Compte utilisateur non trouvé");
        }

        if (account.getRiotPuuid() == null || account.getRiotPuuid().isEmpty())
        {
            return ResponseEntity.status(404).body("Informations Riot non trouvées pour ce compte");
        }

        return ResponseEntity.ok(riotAccountService.getMatchDetails(matchId));
    }


} 