package com.lolgap.project.repositories;

import com.lolgap.project.models.Group;
import com.lolgap.project.models.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByOwnerId(Long ownerId);
    List<Group> findByInvitationsInviteeIdAndInvitationsStatus(Long accountId, Invitation.InvitationStatus status);
    List<Group> findByMembersAccountId(Long accountId);
} 