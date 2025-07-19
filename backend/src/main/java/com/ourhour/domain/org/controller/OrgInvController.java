package com.ourhour.domain.org.controller;

import com.ourhour.domain.org.dto.OrgInvReqDTO;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.org.service.OrgInvService;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.jwt.annotation.OrgAuth;
import com.ourhour.global.jwt.annotation.OrgId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/organizations")
public class OrgInvController {

    private final OrgInvService orgInvService;

    @OrgAuth(accessLevel = Role.ADMIN)
    @PostMapping("/{orgId}/invitation")
    public ResponseEntity<ApiResponse<Void>> sendInvLink(@OrgId @PathVariable Long orgId, @RequestBody OrgInvReqDTO orgInvReqDTO) {

        orgInvService.sendInvLink(orgId, orgInvReqDTO);

        ApiResponse<Void> apiResponse = ApiResponse.success(null, "초대 메일이 발송되었습니다.");

        return ResponseEntity.ok(apiResponse);
    }

}
