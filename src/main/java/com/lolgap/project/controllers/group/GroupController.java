package com.lolgap.project.controllers.group;

import com.lolgap.project.models.Account;
import com.lolgap.project.models.Group;
import com.lolgap.project.models.Invitation;
import com.lolgap.project.repositories.AccountRepository;
import com.lolgap.project.services.GroupService;
import com.lolgap.project.services.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final AccountRepository accountRepository;
    private final InvitationService invitationService;

    private Account getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("Current username: " + username);
        Account account = accountRepository.findByUsername(username);
        System.out.println("Found account: " + (account != null ? account.getUsername() : "null"));
        return account;
    }

    @GetMapping
    public ResponseEntity<String> getTest() {
        return ResponseEntity.ok("test deux");
    }

    @GetMapping("/myGroups")
    public ResponseEntity<List<Group>> getMyGroups() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            String username = authentication.getName();
            Account account = accountRepository.findByUsername(username);
            
            if (account == null) {
                return ResponseEntity.notFound().build();
            }
        
            List<Group> groups = groupService.getUserGroups(account).get();
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            System.out.println("Error getting my groups: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(List.of());
        }
    }

    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            System.out.println("Creating group with name: " + name);
            Group group = groupService.createGroup(name, getCurrentUser()).get();
            System.out.println("Group created successfully: " + group.getName());
            return ResponseEntity.ok(group);
        } catch (Exception e) {
            System.out.println("Error creating group: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{groupId}/name")
    public ResponseEntity<Group> renameGroup(
            @PathVariable Long groupId,
            @RequestBody Map<String, String> request) {
        try {
            String newName = request.get("name");
            if (newName == null || newName.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            Group group = groupService.renameGroup(groupId, getCurrentUser(), newName).get();
            return ResponseEntity.ok(group);
        } catch (Exception e) {
            System.out.println("Error renaming group: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long groupId) {
        try {
            groupService.deleteGroup(groupId, getCurrentUser());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Error deleting group: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{groupId}/leave")
    public ResponseEntity<Void> leaveGroup(@PathVariable Long groupId) {
        try {
            groupService.leaveGroup(groupId, getCurrentUser());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Error leaving group: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
} 