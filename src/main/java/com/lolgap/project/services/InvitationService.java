package com.lolgap.project.services;

import com.lolgap.project.models.Account;
import com.lolgap.project.models.Group;
import com.lolgap.project.models.Invitation;
import com.lolgap.project.repositories.GroupRepository;
import com.lolgap.project.repositories.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class InvitationService {
    private final InvitationRepository invitationRepository;
    private final GroupRepository groupRepository;
    private final MemberService memberService;

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

    @Transactional
    public Invitation respondToInvitation(Long invitationId, Account account, boolean accept) {
        Invitation invitation = invitationRepository.findById(invitationId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation not found"));
        
        if (!invitation.getInvitee().getId().equals(account.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only respond to your own invitations");
        }
        
        if (invitation.getStatus() != Invitation.InvitationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invitation is not pending");
        }
        
        invitation.setStatus(accept ? Invitation.InvitationStatus.ACCEPTED : Invitation.InvitationStatus.REJECTED);
        invitation.setRespondedAt(LocalDateTime.now());
        invitation = invitationRepository.save(invitation);
        
        if (accept) {
            memberService.addMember(invitation.getGroup(), account);
        }
        
        return invitation;
    }

    public CompletableFuture<List<Invitation>> getGroupInvitations(Long groupId) {
        return CompletableFuture.supplyAsync(() -> 
            invitationRepository.findByGroupId(groupId)
        );
    }

    public CompletableFuture<List<Invitation>> getUserInvitations(Account account) {
        return CompletableFuture.supplyAsync(() -> 
            invitationRepository.findByInviteeId(account.getId())
        );
    }
} 