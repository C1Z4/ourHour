package com.ourhour.domain.org.controller;

import com.ourhour.domain.org.dto.OrgReqDTO;
import com.ourhour.domain.org.dto.OrgResDTO;
import com.ourhour.domain.org.service.OrgService;
import com.ourhour.global.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrgController {

    private final OrgService orgService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrgResDTO>> registerOrg(@Valid @RequestBody OrgReqDTO orgReqDTO) {
        OrgResDTO orgResDTO = orgService.registerOrg(orgReqDTO);

        return ResponseEntity.ok(ApiResponse.success(orgResDTO, "팀 등록에 성공하였습니다."));
    }

    @GetMapping("/{orgId}")
    public ResponseEntity<ApiResponse<OrgResDTO>> getOrgInfo(@PathVariable Long orgId) {
        OrgResDTO orgResDTO = orgService.getOrgInfo(orgId);

        return ResponseEntity.ok(ApiResponse.success(orgResDTO, "팀 조회에 성공하였습니다."));
    }
}
