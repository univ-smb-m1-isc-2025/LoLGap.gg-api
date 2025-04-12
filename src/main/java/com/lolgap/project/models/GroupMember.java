package com.lolgap.project.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "group_members")
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false, length = 255)
    private String status = "ACCEPTED";
} 