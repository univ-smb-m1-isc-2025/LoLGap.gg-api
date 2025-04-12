package com.lolgap.project.controllers.group;

import com.lolgap.project.models.Account;
import com.lolgap.project.models.GroupMember;
import com.lolgap.project.repositories.AccountRepository;
import com.lolgap.project.services.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups/{groupId}/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final AccountRepository accountRepository;

    private Account getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return accountRepository.findByUsername(username);
    }

    @GetMapping
    public ResponseEntity<List<GroupMember>> getGroupMembers(@PathVariable Long groupId) {
        try {
            List<GroupMember> members = memberService.getGroupMembers(groupId).get();
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            System.out.println("Error getting group members: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long groupId,
            @PathVariable String username) {
        try {
            Account memberToRemove = accountRepository.findByUsername(username);
            if (memberToRemove == null) {
                return ResponseEntity.notFound().build();
            }
            
            memberService.removeMember(groupId, getCurrentUser(), memberToRemove).get();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Error removing member: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{username}/status")
    public ResponseEntity<GroupMember> updateMemberStatus(
            @PathVariable Long groupId,
            @PathVariable String username,
            @RequestBody Map<String, String> request) {
        try {
            Account memberToUpdate = accountRepository.findByUsername(username);
            if (memberToUpdate == null) {
                return ResponseEntity.notFound().build();
            }

            String statusStr = request.get("status");
            if (statusStr == null || statusStr.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }

            GroupMember updatedMember = memberService.updateMemberStatus(groupId, getCurrentUser(), memberToUpdate, statusStr.trim());
            return ResponseEntity.ok(updatedMember);
        } catch (Exception e) {
            System.out.println("Error updating member status: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }
} 