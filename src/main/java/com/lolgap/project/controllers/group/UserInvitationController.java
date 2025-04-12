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
import java.util.Map;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class UserInvitationController {
    private final InvitationService invitationService;
    private final AccountRepository accountRepository;

    private Account getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("Current username: " + username);
        Account account = accountRepository.findByUsername(username);
        System.out.println("Found account: " + (account != null ? account.getUsername() : "null"));
        return account;
    }

    @GetMapping
    public ResponseEntity<List<Invitation>> getMyInvitations() {
        try {
            List<Invitation> invitations = invitationService.getUserInvitations(getCurrentUser()).get();
            return ResponseEntity.ok(invitations);
        } catch (Exception e) {
            System.out.println("Error getting invitations: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{invitationId}/respond")
    public ResponseEntity<Invitation> respondToInvitation(
            @PathVariable Long invitationId,
            @RequestBody Map<String, Boolean> request) {
        try {
            boolean accept = request.getOrDefault("accept", false);
            Invitation invitation = invitationService.respondToInvitation(invitationId, getCurrentUser(), accept);
            return ResponseEntity.ok(invitation);
        } catch (Exception e) {
            System.out.println("Error responding to invitation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
} 