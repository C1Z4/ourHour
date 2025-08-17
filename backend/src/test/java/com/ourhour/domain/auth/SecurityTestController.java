package com.ourhour.domain.auth;

import com.ourhour.domain.org.enums.Role;
import com.ourhour.global.jwt.annotation.OrgAuth;
import com.ourhour.global.jwt.annotation.OrgId;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ActiveProfiles("test")
@RestController
@RequestMapping("/api/test")
public class SecurityTestController {

    @GetMapping("/auth-check")
    public ResponseEntity<String> authCheck() {
        return ResponseEntity.ok("인증 성공");
    }

    @OrgAuth(accessLevel = Role.ADMIN)
    @GetMapping("/access-check/{orgId}")
    public ResponseEntity<String> accessCheck(@OrgId @PathVariable Long orgId) {
        return ResponseEntity.ok("인가 성공");
    }

}
