package com.lolgap.project.controllers.riotme;

import com.lolgap.project.dto.RiotAccountDTO;
import com.lolgap.project.services.RiotAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/riot/me")
@RequiredArgsConstructor
public class RiotMeAccountController
{
    private final RiotAccount riotAccount;

    @GetMapping()
    public ResponseEntity<?> getMyRiotAccount()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try
        {
            RiotAccountDTO account = riotAccount.ofUsername(auth.getName()).get();
            return ResponseEntity.ok(account);
        } catch (Exception e)
        {
            return ResponseEntity.notFound().build();
        }
    }
} 