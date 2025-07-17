package com.ourhour.domain.org.controller;

import java.util.List;

import com.ourhour.domain.member.dto.MemberInfoResDTO;
import com.ourhour.domain.org.dto.OrgDetailReqDTO;
import com.ourhour.domain.org.dto.OrgDetailResDTO;
import com.ourhour.domain.org.dto.OrgMemberRoleReqDTO;
import com.ourhour.domain.org.dto.OrgMemberRoleResDTO;
import com.ourhour.domain.org.dto.OrgReqDTO;
import com.ourhour.domain.org.dto.OrgResDTO;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.org.service.OrgService;
import com.ourhour.domain.project.dto.ProjectNameResDTO;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.common.dto.PageResponse;
import com.ourhour.global.exception.BusinessException;

import com.ourhour.global.jwt.annotation.OrgAuth;
import com.ourhour.global.jwt.annotation.OrgId;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrgController {

    private final OrgService orgService;

    // 회사 등록
    @PostMapping
    public ResponseEntity<ApiResponse<OrgResDTO>> registerOrg(@Valid @RequestBody OrgReqDTO orgReqDTO) {
        OrgResDTO orgResDTO = orgService.registerOrg(orgReqDTO);

        return ResponseEntity.ok(ApiResponse.success(orgResDTO, "팀 등록에 성공하였습니다."));
    }

    // 회사 정보 조회
    @OrgAuth
    @GetMapping("/{orgId}")
    public ResponseEntity<ApiResponse<OrgDetailResDTO>> getOrgInfo(@OrgId @PathVariable Long orgId) {
        OrgDetailResDTO orgDetailResDTO = orgService.getOrgInfo(orgId);

        return ResponseEntity.ok(ApiResponse.success(orgDetailResDTO, "팀 조회에 성공하였습니다."));
    }

    // 회사 정보 수정
    @OrgAuth(accessLevel = Role.ROOT_ADMIN)
    @PutMapping("/{orgId}")
    public ResponseEntity<ApiResponse<OrgDetailResDTO>> updateOrg(@OrgId @PathVariable Long orgId,
            @Valid @RequestBody OrgDetailReqDTO orgDetailReqDTO) {
        OrgDetailResDTO orgDetailResDTO = orgService.updateOrg(orgId, orgDetailReqDTO);

        return ResponseEntity.ok(ApiResponse.success(orgDetailResDTO, "팀 수정에 성공하였습니다."));
    }

    // 회사 삭제
    @OrgAuth(accessLevel = Role.ROOT_ADMIN)
    @DeleteMapping("/{orgId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrg(@OrgId @PathVariable Long orgId) {
        orgService.deleteOrg(orgId);

        return ResponseEntity.ok(ApiResponse.success(null, "팀 삭제에 성공하였습니다."));
    }



    // 본인이 참여 중인 프로젝트 이름 목록 조회(좌측 사이드바)
    @OrgAuth
    @GetMapping("/{orgId}/projects/my")
    public ResponseEntity<ApiResponse<List<ProjectNameResDTO>>> getMyProjects(@OrgId @PathVariable Long orgId) {

        Long memberId = 2L;
        List<ProjectNameResDTO> response = orgService.getMyProjects(orgId, memberId);
        return ResponseEntity.ok(ApiResponse.success(response, "본인이 참여 중인 프로젝트 이름 목록 조회에 성공하였습니다."));
    }

}
