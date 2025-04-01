package com.lolgap.project.services;

import com.lolgap.project.dto.RiotAccountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;
import com.lolgap.project.models.Account;
import com.lolgap.project.repositories.AccountRepository;

@Service
public class RiotAccount
{
    @Value("${riot.api.key}")
    private String riotApiKey;

    @Value("${client.riot.url}")
    private String riotClientUrl;

    private WebClient riotClient;

    @Autowired
    private LeagueAccount leagueAccount;

    @Autowired
    private AccountRepository accountRepository;

    @PostConstruct
    public void init()
    {
        this.riotClient = WebClient.create(riotClientUrl);
    }

    // Récupérer un compte Riot par Riot ID
    public CompletableFuture<RiotAccountDTO> ofRiotId(String gameName, String tagLine)
    {
        return CompletableFuture.supplyAsync(() -> {
            try
            {
                return riotClient.get()
                    .uri("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}", gameName, tagLine)
                    .header("X-Riot-Token", riotApiKey)
                    .retrieve()
                    .bodyToMono(RiotAccountDTO.class)
                    .block();
            } catch (Exception e)
            {
                throw new RuntimeException("Error fetching Riot account: " + e.getMessage(), e);
            }
        });
    }

    public CompletableFuture<Account> enrich(Account account) {
        return this.ofRiotId(account.getRiotGameName(), account.getRiotTagLine())
            .thenCompose(riotInfo -> {
                if (riotInfo == null) {
                    throw new RuntimeException("Compte Riot invalide");
                }
                
                account.setRiotPuuid(riotInfo.getPuuid());
                
                return leagueAccount.ofPuuid(riotInfo.getPuuid())
                    .thenApply(leagueInfo -> {
                        if (leagueInfo == null) {
                            throw new RuntimeException("Compte League of Legends non trouvé");
                        }
                        
                        account.setSummonerId(leagueInfo.getId());
                        account.setAccountId(leagueInfo.getAccountId());
                        
                        return account;
                    });
            });
    }

    // Valider l'existence d'un compte Riot
    public CompletableFuture<Boolean> validationOfRiotId(String gameName, String tagLine)
    {
        return this.ofRiotId(gameName, tagLine)
            .thenApply(account -> account != null)
            .exceptionally(throwable -> false);
    }

    // Récupérer un compte Riot par username de l'application
    public CompletableFuture<RiotAccountDTO> ofUsername(String username) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Account account = accountRepository.findByUsername(username);
                if (account == null || account.getRiotGameName() == null || account.getRiotTagLine() == null) {
                    throw new RuntimeException("No Riot account found for username: " + username);
                }
                return ofRiotId(account.getRiotGameName(), account.getRiotTagLine()).get();
            } catch (Exception e) {
                throw new RuntimeException("Error fetching Riot account by username: " + e.getMessage(), e);
            }
        });
    }

    // Récupérer un compte Riot par PUUID
    public CompletableFuture<RiotAccountDTO> ofPuuid(String puuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return riotClient.get()
                    .uri("/riot/account/v1/accounts/by-puuid/{puuid}", puuid)
                    .header("X-Riot-Token", riotApiKey)
                    .retrieve()
                    .bodyToMono(RiotAccountDTO.class)
                    .block();
            } catch (Exception e) {
                throw new RuntimeException("Error fetching Riot account by PUUID: " + e.getMessage(), e);
            }
        });
    }
} 