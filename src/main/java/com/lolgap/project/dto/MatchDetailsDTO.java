package com.lolgap.project.dto;

import lombok.Data;

@Data
public class MatchDetailsDTO {
    private MatchMetadataDTO metadata;
    private MatchInfoDTO info;
    
    @Data
    public static class MatchMetadataDTO {
        private String matchId;
        private String[] participants;
    }
    
    @Data
    public static class MatchInfoDTO {
        private long gameCreation;
        private long gameDuration;
        private String gameMode;
        private ParticipantDTO[] participants;
    }
    
    @Data
    public static class ParticipantDTO {
        private String championName;
        private String championId;
        private int kills;
        private int deaths;
        private int assists;
        private boolean win;
        private String puuid;
        private String riotIdGameName;
        private String riotIdTagline;
    }
} 