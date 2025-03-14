package com.lolgap.project.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Account
{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String username;
    private String password;
    private String riotGameName;
    private String riotTagLine;
    
    private String riotPuuid;
    
    private String summonerId;
    private String accountId;
}
