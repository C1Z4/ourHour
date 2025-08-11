package com.ourhour.domain.org.controller;

import com.ourhour.domain.member.dto.MemberInfoResDTO;
import com.ourhour.domain.org.dto.OrgMemberRoleReqDTO;
import com.ourhour.domain.org.dto.OrgMemberRoleResDTO;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.org.service.OrgMemberService;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.common.dto.PageResponse;
import com.ourhour.global.jwt.annotation.OrgAuth;
import com.ourhour.global.jwt.annotation.OrgId;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
@Tag(name = "조직 구성원", description = "조직 구성원 조회/권한/관리 API")
public class OrgMemberController {

    private final OrgMemberService orgMemberService;

    // 구성원 목록 조회
    @OrgAuth
    @GetMapping("/{orgId}/members")
    @Operation(summary = "구성원 목록 조회", description = "조직 구성원 목록을 페이지네이션으로 조회합니다.")
    public ResponseEntity<ApiResponse<PageResponse<MemberInfoResDTO>>> getOrgMembers(
            @OrgId @PathVariable @Min(value = 1, message = "팀 ID는 1 이상이어야 합니다.") Long orgId,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "페이지 번호는 0 이상이어야 합니다.") int currentPage,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.") @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.") int size) {

        Pageable pageable = PageRequest.of(currentPage - 1, size);

        PageResponse<MemberInfoResDTO> response = orgMemberService.getOrgMembers(orgId, pageable);

        return ResponseEntity.ok(ApiResponse.success(response, "팀 구성원 조회에 성공하였습니다."));
    }

    @OrgAuth
    @GetMapping("/{orgId}/members/all")
    @Operation(summary = "구성원 전체 목록 조회", description = "조직의 전체 구성원을 조회합니다.")
    public ResponseEntity<ApiResponse<List<MemberInfoResDTO>>> getAllOrgMembers(
            @OrgId @PathVariable @Min(value = 1, message = "팀 ID는 1 이상이어야 합니다.") Long orgId) {

        List<MemberInfoResDTO> response = orgMemberService.getAllOrgMembers(orgId);

        return ResponseEntity.ok(ApiResponse.success(response, "전체 구성원 조회에 성공하였습니다."));
    }

    // 구성원 상세 조회
    @OrgAuth
    @GetMapping("/{orgId}/members/{memberId}")
    @Operation(summary = "구성원 상세 조회", description = "특정 구성원의 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<MemberInfoResDTO>> getOrgMember(@OrgId @PathVariable Long orgId,
            @PathVariable Long memberId) {

        MemberInfoResDTO memberInfoResDTO = orgMemberService.getOrgMember(orgId, memberId);

        ApiResponse<MemberInfoResDTO> apiResponse = ApiResponse.success(memberInfoResDTO, "구성원 상세 조회에 성공하셨습니다.");

        return ResponseEntity.ok(apiResponse);

    }

    // 구성원 권한 변경
    @OrgAuth(accessLevel = Role.ROOT_ADMIN)
    @PatchMapping("/{orgId}/members/{memberId}/role")
    @Operation(summary = "구성원 권한 변경", description = "특정 구성원의 역할을 변경합니다.")
    public ResponseEntity<ApiResponse<OrgMemberRoleResDTO>> getOrgMember(@OrgId @PathVariable Long orgId,
            @PathVariable Long memberId, @RequestBody OrgMemberRoleReqDTO orgMemberRoleReqDTO) {

        OrgMemberRoleResDTO orgMemberRoleResDTO = orgMemberService.changeRole(orgId, memberId, orgMemberRoleReqDTO);

        ApiResponse<OrgMemberRoleResDTO> apiResponse = ApiResponse.success(orgMemberRoleResDTO, "구성원 변경 권한에 성공하셨습니다.");

        return ResponseEntity.ok(apiResponse);

    }

    // 구성원 삭제
    @OrgAuth(accessLevel = Role.ROOT_ADMIN)
    @DeleteMapping("/{orgId}/members/{memberId}")
    @Operation(summary = "구성원 삭제", description = "조직에서 특정 구성원을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteOrgMember(@OrgId @PathVariable Long orgId,
            @PathVariable Long memberId) {

        orgMemberService.deleteOrgMember(orgId, memberId);

        ApiResponse<Void> apiResponse = ApiResponse.success(null, "팀 구성원 삭제에 성공하였습니다.");

        return ResponseEntity.ok(apiResponse);

    }

    // 회사 나가기
    @OrgAuth
    @DeleteMapping("/{orgId}/members/me")
    @Operation(summary = "조직 나가기", description = "현재 사용자가 조직에서 탈퇴합니다.")
    public ResponseEntity<ApiResponse<Void>> exitOrg(@OrgId @PathVariable Long orgId) {

        orgMemberService.exitOrg(orgId);

        return ResponseEntity.ok(ApiResponse.success(null, "회사 나가기에 성공하였습니다."));
    }

}
