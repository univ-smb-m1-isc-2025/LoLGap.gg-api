package com.lolgap.project.repositories;

import com.lolgap.project.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);
}