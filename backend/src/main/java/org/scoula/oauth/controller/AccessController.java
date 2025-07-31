package org.scoula.oauth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/check-access")
public class AccessController {
    @GetMapping("/safety-check")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> checkSafetyCheckAccess() {
        return ResponseEntity.ok("AUTHORIZED");
    }


    @GetMapping("/agreement")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> checkAgreementCheckAccess() {
        return ResponseEntity.ok("AUTHORIZED");
    }

}
