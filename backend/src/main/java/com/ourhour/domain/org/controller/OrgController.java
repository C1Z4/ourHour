package com.ourhour.domain.org.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ourhour.domain.org.dto.*;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.org.service.DepartmentService;
import com.ourhour.domain.org.service.OrgMemberService;
import com.ourhour.domain.org.service.OrgService;
import com.ourhour.domain.org.service.PositionService;
import com.ourhour.domain.project.dto.ProjectNameResDTO;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.domain.auth.exception.AuthException;
import com.ourhour.domain.member.dto.MemberInfoResDTO;
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
    private final OrgMemberService orgMemberService;
    private final DepartmentService departmentService;
    private final PositionService positionService;

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

    // 부서 생성
    @OrgAuth(accessLevel = Role.ADMIN)
    @PostMapping("/{orgId}/departments")
    @Operation(summary = "부서 생성", description = "새 부서를 생성합니다.")
    public ResponseEntity<ApiResponse<DepartmentResDTO>> createDepartment(
            @OrgId @PathVariable Long orgId,
            @Valid @RequestBody DepartmentReqDTO departmentReqDTO) {
        DepartmentResDTO response = departmentService.createDepartment(departmentReqDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "부서 생성에 성공하였습니다."));
    }

    // 부서 목록 조회
    @OrgAuth
    @GetMapping("/{orgId}/departments")
    @Operation(summary = "조직 부서 목록 조회", description = "특정 조직의 부서 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<DepartmentResDTO>>> getDepartmentsByOrg(@OrgId @PathVariable Long orgId) {
        List<DepartmentResDTO> response = departmentService.getDepartmentsByOrg(orgId);
        return ResponseEntity.ok(ApiResponse.success(response, "조직 부서 목록 조회에 성공하였습니다."));
    }

    // 부서 수정
    @OrgAuth(accessLevel = Role.ADMIN)
    @PutMapping("/{orgId}/departments/{deptId}")
    @Operation(summary = "부서 정보 수정", description = "부서 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<DepartmentResDTO>> updateDepartment(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long deptId,
            @Valid @RequestBody DepartmentReqDTO departmentReqDTO) {
        DepartmentResDTO response = departmentService.updateDepartment(deptId, departmentReqDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "부서 수정에 성공하였습니다."));
    }

    // 부서 삭제
    @OrgAuth(accessLevel = Role.ADMIN)
    @DeleteMapping("/{orgId}/departments/{deptId}")
    @Operation(summary = "부서 삭제", description = "부서를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long deptId) {
        departmentService.deleteDepartment(deptId);
        return ResponseEntity.ok(ApiResponse.success(null, "부서 삭제에 성공하였습니다."));
    }

    // 직책 생성
    @OrgAuth(accessLevel = Role.ADMIN)
    @PostMapping("/{orgId}/positions")
    @Operation(summary = "직책 생성", description = "새 직책을 생성합니다.")
    public ResponseEntity<ApiResponse<PositionResDTO>> createPosition(
            @OrgId @PathVariable Long orgId,
            @Valid @RequestBody PositionReqDTO positionReqDTO) {
        PositionResDTO response = positionService.createPosition(positionReqDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "직책 생성에 성공하였습니다."));
    }

    // 직책 목록 조회
    @OrgAuth
    @GetMapping("/{orgId}/positions")
    @Operation(summary = "조직 직책 목록 조회", description = "특정 조직의 직책 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<PositionResDTO>>> getPositionsByOrg(@OrgId @PathVariable Long orgId) {
        List<PositionResDTO> response = positionService.getPositionsByOrg(orgId);
        return ResponseEntity.ok(ApiResponse.success(response, "조직 직책 목록 조회에 성공하였습니다."));
    }

    // 직책 수정
    @OrgAuth(accessLevel = Role.ADMIN)
    @PutMapping("/{orgId}/positions/{positionId}")
    @Operation(summary = "직책 정보 수정", description = "직책 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<PositionResDTO>> updatePosition(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long positionId,
            @Valid @RequestBody PositionReqDTO positionReqDTO) {
        PositionResDTO response = positionService.updatePosition(positionId, positionReqDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "직책 수정에 성공하였습니다."));
    }

    // 직책 삭제
    @OrgAuth(accessLevel = Role.ADMIN)
    @DeleteMapping("/{orgId}/positions/{positionId}")
    @Operation(summary = "직책 삭제", description = "직책을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deletePosition(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long positionId) {
        positionService.deletePosition(positionId);
        return ResponseEntity.ok(ApiResponse.success(null, "직책 삭제에 성공하였습니다."));
    }

    // ========== 구성원 조회 API ==========
    
    @OrgAuth
    @GetMapping("/{orgId}/departments/{deptId}/members")
    @Operation(summary = "부서별 구성원 조회", description = "특정 부서에 속한 구성원 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<MemberInfoResDTO>>> getMembersByDepartment(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long deptId) {
        List<MemberInfoResDTO> response = orgMemberService.getMembersByDepartment(orgId, deptId);
        return ResponseEntity.ok(ApiResponse.success(response, "부서별 구성원 조회에 성공하였습니다."));
    }
    
    @OrgAuth
    @GetMapping("/{orgId}/positions/{positionId}/members")
    @Operation(summary = "직책별 구성원 조회", description = "특정 직책의 구성원 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<MemberInfoResDTO>>> getMembersByPosition(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long positionId) {
        List<MemberInfoResDTO> response = orgMemberService.getMembersByPosition(orgId, positionId);
        return ResponseEntity.ok(ApiResponse.success(response, "직책별 구성원 조회에 성공하였습니다."));
    }

}
