package com.lolgap.project.services;

import com.lolgap.project.dto.MatchDetailsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.core.ParameterizedTypeReference;
import java.util.concurrent.TimeUnit;
import java.util.Objects;

@Service
public class LeagueMatch
{
    @Value("${riot.api.key}")
    private String riotApiKey;

    @Value("${client.riot.url}")
    private String riotClientUrl;

    @Autowired
    private LeagueAccount leagueAccount;

    @Autowired
    private RiotAccount riotAccount;

    private WebClient riotClient;

    @PostConstruct
    public void init()
    {
        this.riotClient = WebClient.create(riotClientUrl);
    }

    // Récupérer l'historique des matchs par PUUID
    public CompletableFuture<List<String>> ofPuuid(String puuid)
    {
        return CompletableFuture.<List<String>>supplyAsync(() -> {
            try
            {
                return riotClient.get()
                    .uri("/lol/match/v5/matches/by-puuid/{puuid}/ids?start=0&count=5", puuid)
                    .header("X-Riot-Token", riotApiKey)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                    .block();
            } catch (Exception e)
            {
                throw new RuntimeException("Error fetching match history: " + e.getMessage(), e);
            }
        });
    }

    // Récupérer l'historique des matchs par PUUID et queue ID
    public CompletableFuture<List<String>> ofPuuidByQueue(String puuid, int queueId)
    {
        return CompletableFuture.supplyAsync(() -> {
            try
            {
                return riotClient.get()
                    .uri("/lol/match/v5/matches/by-puuid/{puuid}/ids?queue={queueId}&start=0&count=7", puuid, queueId)
                    .header("X-Riot-Token", riotApiKey)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                    .block();
            } catch (Exception e)
            {
                throw new RuntimeException("Error fetching match history by queue: " + e.getMessage(), e);
            }
        });
    }

    // Récupérer l'historique des matchs par username de l'application
    public CompletableFuture<List<String>> ofUsername(String username)
    {
        return leagueAccount.ofUsername(username)
            .thenCompose(account -> this.ofPuuid(account.getPuuid()));
    }

    // Récupérer l'historique des matchs par Riot ID
    public CompletableFuture<List<String>> ofRiotId(String gameName, String tagLine)
    {
        return riotAccount.ofRiotId(gameName, tagLine)
            .thenCompose(account -> this.ofPuuid(account.getPuuid()));
    }

    // Récupérer les détails d'un match spécifique
    public CompletableFuture<MatchDetailsDTO> detailsOfMatch(String matchId)
    {
        return CompletableFuture.supplyAsync(() -> {
            try
            {
                return riotClient.get()
                    .uri("/lol/match/v5/matches/{matchId}", matchId)
                    .header("X-Riot-Token", riotApiKey)
                    .retrieve()
                    .bodyToMono(MatchDetailsDTO.class)
                    .block();
            } catch (Exception e)
            {
                throw new RuntimeException("Error fetching match details: " + e.getMessage(), e);
            }
        });
    }

    // Récupérer les derniers matchs avec détails par username
    public CompletableFuture<List<MatchDetailsDTO>> ofUsernameWithDetails(String username, int count)
    {
        return ofUsername(username)
            .thenCompose(matchIds -> {
                List<CompletableFuture<MatchDetailsDTO>> futures = matchIds.stream()
                    .limit(Math.min(count, 5))
                    .map(matchId -> detailsOfMatch(matchId)
                        .orTimeout(10, TimeUnit.SECONDS))
                    .collect(Collectors.toList());

                return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .orTimeout(30, TimeUnit.SECONDS)
                    .thenApply(v -> futures.stream()
                        .map(future -> {
                            try {
                                return future.get();
                            } catch (Exception e) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));
            });
    }

    // Récupérer les derniers matchs avec détails par Riot ID
    public CompletableFuture<List<MatchDetailsDTO>> ofRiotIdWithDetails(String gameName, String tagLine, int count)
    {
        return ofRiotId(gameName, tagLine)
            .thenCompose(matchIds -> {
                List<CompletableFuture<MatchDetailsDTO>> futures = matchIds.stream()
                    .limit(count)
                    .map(this::detailsOfMatch)
                    .collect(Collectors.toList());
                return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
            });
    }

    // Récupérer les derniers matchs avec détails par puuid
    public CompletableFuture<List<MatchDetailsDTO>> ofPuuidWithDetails(String puuid, int count)
    {
        return ofPuuid(puuid)
            .thenCompose(matchIds -> {
                List<CompletableFuture<MatchDetailsDTO>> futures = matchIds.stream()
                    .limit(count)
                    .map(this::detailsOfMatch)
                    .collect(Collectors.toList());
                return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
            });
    }

    // Récupérer l'historique des matchs par username et queue ID
    public CompletableFuture<List<String>> ofUsernameByQueue(String username, int queueId)
    {
        return leagueAccount.ofUsername(username)
            .thenCompose(account -> this.ofPuuidByQueue(account.getPuuid(), queueId));
    }

    // Récupérer les derniers matchs avec détails par username et queue ID
    public CompletableFuture<List<MatchDetailsDTO>> ofUsernameWithDetailsByQueue(String username, int queueId, int count)
    {
        return ofUsernameByQueue(username, queueId)
            .thenCompose(matchIds -> {
                List<CompletableFuture<MatchDetailsDTO>> futures = matchIds.stream()
                    .limit(Math.min(count, 5))
                    .map(matchId -> detailsOfMatch(matchId)
                        .orTimeout(10, TimeUnit.SECONDS))
                    .collect(Collectors.toList());

                return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .orTimeout(30, TimeUnit.SECONDS)
                    .thenApply(v -> futures.stream()
                        .map(future -> {
                            try {
                                return future.get();
                            } catch (Exception e) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));
            });
    }
}
