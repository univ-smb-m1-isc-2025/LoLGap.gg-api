package com.lolgap.project.services;

import com.lolgap.project.models.Account;
import com.lolgap.project.models.Group;
import com.lolgap.project.models.GroupMember;
import com.lolgap.project.models.Invitation;
import com.lolgap.project.repositories.GroupMemberRepository;
import com.lolgap.project.repositories.GroupRepository;
import com.lolgap.project.repositories.InvitationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class MemberService {
    private final GroupMemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final InvitationRepository invitationRepository;

    public MemberService(GroupMemberRepository memberRepository, GroupRepository groupRepository, InvitationRepository invitationRepository) {
        this.memberRepository = memberRepository;
        this.groupRepository = groupRepository;
        this.invitationRepository = invitationRepository;
    }

    public CompletableFuture<Void> addMember(Group group, Account account) {
        return CompletableFuture.runAsync(() -> {
            GroupMember member = new GroupMember();
            member.setGroup(group);
            member.setAccount(account);
            member.setStatus("ACCEPTED");
            memberRepository.save(member);
        });
    }

    public CompletableFuture<Void> removeMember(Long groupId, Account owner, Account memberToRemove) {
        return CompletableFuture.runAsync(() -> {
            Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
            
            if (!group.getOwner().getId().equals(owner.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the group owner can remove members");
            }
            
            GroupMember member = memberRepository.findByGroupIdAndAccountId(groupId, memberToRemove.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));
            memberRepository.delete(member);
            
            // Update invitation status if it exists
            invitationRepository.findByGroupIdAndInviteeId(groupId, memberToRemove.getId())
                .ifPresent(invitation -> {
                    invitation.setStatus(Invitation.InvitationStatus.REJECTED);
                    invitationRepository.save(invitation);
                });
        });
    }

    public CompletableFuture<List<GroupMember>> getGroupMembers(Long groupId) {
        return CompletableFuture.supplyAsync(() -> 
            memberRepository.findByGroupId(groupId)
        );
    }

    @Transactional
    public GroupMember updateMemberStatus(Long groupId, Account requester, Account memberToUpdate, String status) {
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));

        if (!group.getOwner().getId().equals(requester.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the group owner can update member status");
        }

        GroupMember member = memberRepository.findByGroupIdAndAccountId(groupId, memberToUpdate.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));

        member.setStatus(status);
        return memberRepository.save(member);
    }
} 