package com.lolgap.project.repositories;

import com.lolgap.project.models.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findByGroupId(Long groupId);
    List<Invitation> findByInviteeId(Long inviteeId);
    Optional<Invitation> findByGroupIdAndInviteeId(Long groupId, Long inviteeId);
    List<Invitation> findByGroupIdAndStatus(Long groupId, Invitation.InvitationStatus status);
    List<Invitation> findAllByGroupIdAndInviteeId(Long groupId, Long inviteeId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Invitation i WHERE i.group.id = :groupId")
    void deleteByGroupId(Long groupId);
} 