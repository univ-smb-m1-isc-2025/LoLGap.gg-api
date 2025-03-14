package com.lolgap.project.dto;

import lombok.Data;

@Data
public class LeagueAccountDTO
{
    private String id;
    private String accountId;    
    private String puuid;
    private String name;
    private int profileIconId;
    private int summonerLevel;
    private long revisionDate;
} 