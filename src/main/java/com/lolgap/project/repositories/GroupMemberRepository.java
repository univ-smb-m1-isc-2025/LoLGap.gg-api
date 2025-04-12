package com.lolgap.project.repositories;

import com.lolgap.project.models.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findByGroupId(Long groupId);
    List<GroupMember> findByAccountId(Long accountId);
    Optional<GroupMember> findByGroupIdAndAccountId(Long groupId, Long accountId);
    List<GroupMember> findByGroupIdAndStatus(Long groupId, String status);
    
    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.account.id = :accountId")
    List<GroupMember> findAllByGroupIdAndAccountId(@Param("groupId") Long groupId, @Param("accountId") Long accountId);

    @Modifying
    @Transactional
    @Query("DELETE FROM GroupMember gm WHERE gm.group.id = :groupId")
    void deleteByGroupId(Long groupId);
} 