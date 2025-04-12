package com.lolgap.project.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Set;

@Data
@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Account owner;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private Set<Invitation> invitations;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private Set<GroupMember> members;
}
