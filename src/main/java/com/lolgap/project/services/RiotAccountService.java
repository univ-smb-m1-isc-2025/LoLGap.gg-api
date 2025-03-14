package com.lolgap.project.services;

import com.lolgap.project.dto.RiotAccountDTO;
import com.lolgap.project.dto.LeagueAccountDTO;
import com.lolgap.project.models.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class RiotAccountService {

    @Value("${riot.api.key}")
    private String riotApiKey;

    private final WebClient riotClient;
    private final WebClient leagueClient;

    public RiotAccountService() {
        this.riotClient = WebClient.create("https://europe.api.riotgames.com");
        this.leagueClient = WebClient.create("https://euw1.api.riotgames.com");
    }

    public boolean validateRiotAccount(String gameName, String tagLine)
    {
        try
        {
            return riotClient.get()
                    .uri("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}", gameName, tagLine)
                    .header("X-Riot-Token", riotApiKey)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block() != null;
        } catch (WebClientResponseException.NotFound e)
        {
            return false;
        } catch (Exception e)
        {
            throw new RuntimeException("Erreur lors de la validation du compte Riot", e);
        }
    }

    public RiotAccountDTO getRiotAccountInfo(String gameName, String tagLine)
    {
        try
        {
            return riotClient.get()
                    .uri("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}", gameName, tagLine)
                    .header("X-Riot-Token", riotApiKey)
                    .retrieve()
                    .bodyToMono(RiotAccountDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound e)
        {
            return null;
        } catch (Exception e)
        {
            throw new RuntimeException("Erreur lors de la récupération des informations du compte Riot", e);
        }
    }

    public LeagueAccountDTO getLeagueAccountByPuuid(String puuid)
    {
        if (puuid == null || puuid.isEmpty())
        {
            throw new IllegalArgumentException("PUUID ne peut pas être null ou vide");
        }

        try
        {
            return leagueClient.get()
                    .uri("/lol/summoner/v4/summoners/by-puuid/{puuid}", puuid)
                    .header("X-Riot-Token", riotApiKey)
                    .retrieve()
                    .bodyToMono(LeagueAccountDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound e)
        {
            return null;
        } catch (Exception e)
        {
            throw new RuntimeException("Erreur lors de la récupération des informations du compte League of Legends", e);
        }
    }

    public Account enrichAccountWithRiotInfo(Account account)
    {

        RiotAccountDTO riotInfo = getRiotAccountInfo(account.getRiotGameName(), account.getRiotTagLine());
        if (riotInfo == null)
        {
            throw new RuntimeException("Compte Riot invalide");
        }
        
        account.setRiotPuuid(riotInfo.getPuuid());
        
        LeagueAccountDTO leagueInfo = getLeagueAccountByPuuid(riotInfo.getPuuid());
        if (leagueInfo == null)
        {
            throw new RuntimeException("Compte League of Legends non trouvé");
        }
        
        account.setSummonerId(leagueInfo.getId());
        account.setAccountId(leagueInfo.getAccountId());
        
        return account;
    }
} 