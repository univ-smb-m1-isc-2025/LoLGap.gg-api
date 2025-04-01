package com.lolgap.project.services;

import com.lolgap.project.dto.LeagueEntryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class LeagueRank {
    
    @Value("${riot.api.key}")
    private String riotApiKey;

    @Value("${client.league.url}")
    private String leagueClientUrl;

    @Autowired
    private LeagueAccount leagueAccount;

    @Autowired
    private RiotAccount riotAccount;

    private WebClient leagueClient;

    @PostConstruct
    public void init() {
        this.leagueClient = WebClient.create(leagueClientUrl);
    }

    // Récupérer les rangs par PUUID
    public CompletableFuture<List<LeagueEntryDTO>> ofPuuid(String puuid) {
        return CompletableFuture.<List<LeagueEntryDTO>>supplyAsync(() -> {
            try {
                return leagueClient.get()
                    .uri("/lol/league/v4/entries/by-puuid/{puuid}", puuid)
                    .header("X-Riot-Token", riotApiKey)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<LeagueEntryDTO>>() {})
                    .block();
            } catch (Exception e) {
                throw new RuntimeException("Error fetching ranks: " + e.getMessage(), e);
            }
        });
    }

    // Récupérer les rangs par username de l'application
    public CompletableFuture<List<LeagueEntryDTO>> ofUsername(String username) {
        return leagueAccount.ofUsername(username)
            .thenCompose(account -> this.ofPuuid(account.getPuuid()));
    }

    // Récupérer les rangs par Riot ID
    public CompletableFuture<List<LeagueEntryDTO>> ofRiotId(String gameName, String tagLine) {
        return riotAccount.ofRiotId(gameName, tagLine)
            .thenCompose(account -> this.ofPuuid(account.getPuuid()));
    }

    // Récupérer les rangs par summoner ID
    public CompletableFuture<List<LeagueEntryDTO>> ofSummonerId(String summonerId) {
        return CompletableFuture.<List<LeagueEntryDTO>>supplyAsync(() -> {
            try {
                return leagueClient.get()
                    .uri("/lol/league/v4/entries/by-summoner/{summonerId}", summonerId)
                    .header("X-Riot-Token", riotApiKey)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<LeagueEntryDTO>>() {})
                    .block();
            } catch (Exception e) {
                throw new RuntimeException("Error fetching ranks by summoner ID: " + e.getMessage(), e);
            }
        });
    }
} 