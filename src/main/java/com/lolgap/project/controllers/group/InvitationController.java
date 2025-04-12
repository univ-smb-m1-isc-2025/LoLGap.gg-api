package com.lolgap.project.controllers.group;

import com.lolgap.project.models.Account;
import com.lolgap.project.models.Invitation;
import com.lolgap.project.repositories.AccountRepository;
import com.lolgap.project.services.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/invitations")
@RequiredArgsConstructor
public class InvitationController {
    private final InvitationService invitationService;
    private final AccountRepository accountRepository;

    private Account getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return accountRepository.findByUsername(username);
    }

    @PostMapping("/{username}")
    public ResponseEntity<Invitation> inviteMember(
            @PathVariable Long groupId,
            @PathVariable String username) {
        try {
            Account invitee = accountRepository.findByUsername(username);
            if (invitee == null) {
                return ResponseEntity.notFound().build();
            }
            
            Invitation invitation = invitationService.inviteMember(groupId, getCurrentUser(), invitee).get();
            return ResponseEntity.ok(invitation);
        } catch (Exception e) {
            System.out.println("Error inviting member: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    

    @GetMapping
    public ResponseEntity<List<Invitation>> getGroupInvitations(@PathVariable Long groupId) {
        try {
            List<Invitation> invitations = invitationService.getGroupInvitations(groupId).get();
            return ResponseEntity.ok(invitations);
        } catch (Exception e) {
            System.out.println("Error getting group invitations: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
} 