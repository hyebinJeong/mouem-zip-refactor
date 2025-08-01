package org.scoula.oauth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/safety-check/{registerId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> checkRegisterResultAccess(@PathVariable Long registerId) {
        return ResponseEntity.ok("AUTHORIZED");
    }

    // 마이페이지
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> checkMyPageAccess() {
        return ResponseEntity.ok("AUTHORIZED");
    }

//    // 체크리스트 (개인 분석용) 아직 라우터에 구현 안되어있음
//    @GetMapping("/checklist/{registerId}")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<?> checkChecklistAccess(@PathVariable Long registerId) {
//        return ResponseEntity.ok("AUTHORIZED");
//    }
}
