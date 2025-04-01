package com.lolgap.project.services;

import com.lolgap.project.dto.LeagueAccountDTO;
import com.lolgap.project.dto.RiotAccountDTO;
import com.lolgap.project.models.Account;
import com.lolgap.project.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.concurrent.CompletableFuture;
import jakarta.annotation.PostConstruct;

@Service
public class LeagueAccount
{
    @Value("${riot.api.key}")
    private String riotApiKey;

    @Autowired
    private AccountRepository accountRepository;

    @Value("${client.riot.url}")
    private String riotClientUrl;

    @Value("${client.league.url}")
    private String leagueClientUrl;

    private WebClient riotClient;
    private WebClient leagueClient;

    @PostConstruct
    public void init() {
        this.riotClient = WebClient.create(riotClientUrl);
        this.leagueClient = WebClient.create(leagueClientUrl);
    }
    public CompletableFuture<LeagueAccountDTO> ofUsername(String username) 
    {
        return CompletableFuture.supplyAsync(() -> {
            try
            {
                Account account = accountRepository.findByUsername(username);
                if (account == null || account.getRiotPuuid() == null || account.getRiotPuuid().isEmpty())
                {
                    throw new RuntimeException("Account not found or no Riot PUUID available for username: " + username);
                }

                return ofPuuid(account.getRiotPuuid()).get();
            } catch (WebClientResponseException.NotFound e)
            {
                throw new RuntimeException("League account not found for username: " + username);
            } catch (Exception e)
            {
                throw new RuntimeException("Error fetching account by username: " + e.getMessage(), e);
            }
        });
    }
    
    public CompletableFuture<LeagueAccountDTO> ofPuuid(String puuid)
    {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return leagueClient.get()
                    .uri("/lol/summoner/v4/summoners/by-puuid/{puuid}", puuid)
                    .header("X-Riot-Token", riotApiKey)
                    .retrieve()
                    .bodyToMono(LeagueAccountDTO.class)
                    .block();
            } catch (WebClientResponseException.NotFound e)
            {
                throw new RuntimeException("Summoner not found for PUUID: " + puuid);
            } catch (Exception e)
            {
                throw new RuntimeException("Error fetching summoner by PUUID: " + e.getMessage(), e);
            }
        });
    }

    public CompletableFuture<LeagueAccountDTO> ofRiotId(String gameName, String tagLine)
    {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // First, get the Riot account info to get the PUUID
                RiotAccountDTO riotAccount = riotClient.get()
                    .uri("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}", gameName, tagLine)
                    .header("X-Riot-Token", riotApiKey)
                    .retrieve()
                    .bodyToMono(RiotAccountDTO.class)
                    .block();

                if (riotAccount == null)
                {
                    throw new RuntimeException("Riot account not found: " + gameName + "#" + tagLine);
                }

                // Then, get the League account info using the PUUID
                return leagueClient.get()
                    .uri("/lol/summoner/v4/summoners/by-puuid/{puuid}", riotAccount.getPuuid())
                    .header("X-Riot-Token", riotApiKey)
                    .retrieve()
                    .bodyToMono(LeagueAccountDTO.class)
                    .block();
            } catch (WebClientResponseException.NotFound e)
            {
                throw new RuntimeException("Account not found: " + gameName + "#" + tagLine);
            } catch (Exception e)
            {
                throw new RuntimeException("Error fetching account by Riot ID: " + e.getMessage(), e);
            }
        });
    }
}
