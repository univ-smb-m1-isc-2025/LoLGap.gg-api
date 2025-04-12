package com.lolgap.project.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "invitations")
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "inviter_id", nullable = false)
    private Account inviter;

    @ManyToOne
    @JoinColumn(name = "invitee_id", nullable = false)
    private Account invitee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status = InvitationStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime respondedAt;

    public enum InvitationStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    @PreUpdate
    public void preUpdate() {
        if (status != InvitationStatus.PENDING && respondedAt == null) {
            respondedAt = LocalDateTime.now();
        }
    }
}
