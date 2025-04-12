package com.lolgap.project.services;

import com.lolgap.project.models.Account;
import com.lolgap.project.models.Group;
import com.lolgap.project.models.GroupMember;
import com.lolgap.project.models.Invitation;
import com.lolgap.project.repositories.GroupRepository;
import com.lolgap.project.repositories.GroupMemberRepository;
import com.lolgap.project.repositories.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final InvitationRepository invitationRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberService memberService;
    private final InvitationService invitationService;

    public CompletableFuture<Group> createGroup(String name, Account owner) {
        return CompletableFuture.supplyAsync(() -> {
            Group group = new Group();
            System.out.println("Creating group with name: " + name);
            System.out.println("Owner: " + owner.getUsername());
            group.setName(name);
            group.setOwner(owner);
            
            Group savedGroup = groupRepository.save(group);
            
            // Add owner as member
            memberService.addMember(savedGroup, owner).join();
            
            System.out.println("Group saved: " + savedGroup.getId());
            return savedGroup;
        });
    }

    public CompletableFuture<Invitation> inviteMember(Long groupId, Account inviter, Account invitee) {
        return CompletableFuture.supplyAsync(() -> {
            Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
            
            if (!group.getOwner().getId().equals(inviter.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only group owner can invite members");
            }
            
            // Check if user has any pending or accepted invitations
            List<Invitation> existingInvitations = invitationRepository.findAllByGroupIdAndInviteeId(groupId, invitee.getId());
            boolean hasActiveInvitation = existingInvitations.stream()
                .anyMatch(inv -> inv.getStatus() == Invitation.InvitationStatus.PENDING || 
                               inv.getStatus() == Invitation.InvitationStatus.ACCEPTED);
            
            if (hasActiveInvitation) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already has an active invitation");
            }
            
            Invitation invitation = new Invitation();
            invitation.setGroup(group);
            invitation.setInviter(inviter);
            invitation.setInvitee(invitee);
            invitation.setStatus(Invitation.InvitationStatus.PENDING);
            
            return invitationRepository.save(invitation);
        });
    }

    public CompletableFuture<Invitation> respondToInvitation(Long groupId, Account account, boolean accept) {
        return CompletableFuture.supplyAsync(() -> {
            Invitation invitation = invitationRepository.findByGroupIdAndInviteeId(groupId, account.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found"));
            
            if (invitation.getStatus() != Invitation.InvitationStatus.PENDING) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invitation is not pending");
            }
            
            invitation.setStatus(accept ? Invitation.InvitationStatus.ACCEPTED : Invitation.InvitationStatus.REJECTED);
            
            if (accept) {
                // Create group member when invitation is accepted
                GroupMember member = new GroupMember();
                member.setGroup(invitation.getGroup());
                member.setAccount(account);
                groupMemberRepository.save(member);
            }
            
            return invitationRepository.save(invitation);
        });
    }

    public CompletableFuture<Void> removeMember(Long groupId, Account owner, Account memberToRemove) {
        return CompletableFuture.runAsync(() -> {
            Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
            
            if (!group.getOwner().getId().equals(owner.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only group owner can remove members");
            }
            
            // Remove from members
            GroupMember member = groupMemberRepository.findByGroupIdAndAccountId(groupId, memberToRemove.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));
            groupMemberRepository.delete(member);
            
            // Update invitation status
            Invitation invitation = invitationRepository.findByGroupIdAndInviteeId(groupId, memberToRemove.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found"));
            invitation.setStatus(Invitation.InvitationStatus.REJECTED);
            invitationRepository.save(invitation);
        });
    }

    public CompletableFuture<Group> renameGroup(Long groupId, Account owner, String newName) {
        return CompletableFuture.supplyAsync(() -> {
            Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
            
            if (!group.getOwner().getId().equals(owner.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only group owner can rename the group");
            }
            
            group.setName(newName);
            return groupRepository.save(group);
        });
    }

    @Transactional
    public void deleteGroup(Long groupId, Account owner) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));

        if (!group.getOwner().getId().equals(owner.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the group owner can delete the group");
        }

        // Supprimer tous les membres du groupe en une seule opération
        groupMemberRepository.deleteByGroupId(groupId);

        // Supprimer toutes les invitations du groupe en une seule opération
        invitationRepository.deleteByGroupId(groupId);

        // Supprimer le groupe
        groupRepository.delete(group);
    }

    public CompletableFuture<List<Group>> getUserGroups(Account account) {
        return CompletableFuture.supplyAsync(() -> {
            if (account == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }
            
            return groupRepository.findByMembersAccountId(account.getId());
        });
    }

    public CompletableFuture<List<Invitation>> getGroupInvitations(Long groupId) {
        return CompletableFuture.supplyAsync(() -> 
            invitationRepository.findByGroupId(groupId)
        );
    }

    public CompletableFuture<List<GroupMember>> getGroupMembers(Long groupId) {
        return CompletableFuture.supplyAsync(() -> 
            groupMemberRepository.findByGroupId(groupId)
        );
    }

    @Transactional
    public void leaveGroup(Long groupId, Account member) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
        
        if (group.getOwner().getId().equals(member.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Group owner cannot leave the group");
        }
        
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndAccountId(groupId, member.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "You are not a member of this group"));
        
        groupMemberRepository.delete(groupMember);
        
        // Update invitation status if exists
        invitationRepository.findByGroupIdAndInviteeId(groupId, member.getId())
            .ifPresent(invitation -> {
                invitation.setStatus(Invitation.InvitationStatus.REJECTED);
                invitationRepository.save(invitation);
            });
    }
} 