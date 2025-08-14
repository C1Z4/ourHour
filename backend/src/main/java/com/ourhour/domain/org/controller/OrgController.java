package com.ourhour.domain.org.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ourhour.domain.org.dto.OrgDetailReqDTO;
import com.ourhour.domain.org.dto.OrgDetailResDTO;
import com.ourhour.domain.org.dto.OrgReqDTO;
import com.ourhour.domain.org.dto.OrgResDTO;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.org.service.OrgService;
import com.ourhour.domain.project.dto.ProjectNameResDTO;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.domain.auth.exception.AuthException;
import com.ourhour.domain.member.exception.MemberException;
import com.ourhour.global.jwt.annotation.OrgAuth;
import com.ourhour.global.jwt.annotation.OrgId;
import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.jwt.dto.OrgAuthority;
import com.ourhour.global.jwt.util.UserContextHolder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
@Tag(name = "조직", description = "조직 정보/프로젝트 목록 관리 API")
public class OrgController {

    private final OrgService orgService;

    // 회사 등록
    @PostMapping
    @Operation(summary = "조직 등록", description = "새 조직을 등록합니다.")
    public ResponseEntity<ApiResponse<OrgResDTO>> registerOrg(@Valid @RequestBody OrgReqDTO orgReqDTO) {
        OrgResDTO orgResDTO = orgService.registerOrg(orgReqDTO);

        return ResponseEntity.ok(ApiResponse.success(orgResDTO, "팀 등록에 성공하였습니다."));
    }

    // 회사 정보 조회
    @OrgAuth
    @GetMapping("/{orgId}")
    @Operation(summary = "조직 정보 조회", description = "특정 조직의 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<OrgDetailResDTO>> getOrgInfo(@OrgId @PathVariable Long orgId) {
        OrgDetailResDTO orgDetailResDTO = orgService.getOrgInfo(orgId);

        return ResponseEntity.ok(ApiResponse.success(orgDetailResDTO, "팀 조회에 성공하였습니다."));
    }

    // 회사 정보 수정
    @OrgAuth(accessLevel = Role.ROOT_ADMIN)
    @PutMapping("/{orgId}")
    @Operation(summary = "조직 정보 수정", description = "특정 조직의 상세 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<OrgDetailResDTO>> updateOrg(@OrgId @PathVariable Long orgId,
            @Valid @RequestBody OrgDetailReqDTO orgDetailReqDTO) {
        OrgDetailResDTO orgDetailResDTO = orgService.updateOrg(orgId, orgDetailReqDTO);

        return ResponseEntity.ok(ApiResponse.success(orgDetailResDTO, "팀 수정에 성공하였습니다."));
    }

    // 회사 삭제
    @OrgAuth(accessLevel = Role.ROOT_ADMIN)
    @DeleteMapping("/{orgId}")
    @Operation(summary = "조직 삭제", description = "특정 조직을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteOrg(@OrgId @PathVariable Long orgId) {
        orgService.deleteOrg(orgId);

        return ResponseEntity.ok(ApiResponse.success(null, "팀 삭제에 성공하였습니다."));
    }

    // 본인이 참여 중인 프로젝트 이름 목록 조회(좌측 사이드바)
    @OrgAuth
    @GetMapping("/{orgId}/projects")
    @Operation(summary = "내 프로젝트 목록 조회", description = "조직 내 본인이 참여 중인 프로젝트 이름 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ProjectNameResDTO>>> getMyProjects(@OrgId @PathVariable Long orgId) {

        Claims claims = UserContextHolder.get();
        if (claims == null) {
            throw AuthException.unauthorizedException();
        }

        Long memberId = claims.getOrgAuthorityList().stream()
                .filter(authority -> authority.getOrgId().equals(orgId))
                .map(OrgAuthority::getMemberId)
                .findFirst()
                .orElseThrow(() -> MemberException.memberAccessDeniedException());

        List<ProjectNameResDTO> response = orgService.getMyProjects(memberId, orgId);

        return ResponseEntity.ok(ApiResponse.success(response, "본인이 참여 중인 프로젝트 이름 목록 조회에 성공하였습니다."));
    }

}
