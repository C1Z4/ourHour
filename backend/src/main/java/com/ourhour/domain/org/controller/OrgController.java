package com.ourhour.domain.org.controller;

import com.ourhour.domain.member.dto.MemberInfoResDTO;
import com.ourhour.domain.org.dto.OrgReqDTO;
import com.ourhour.domain.org.dto.OrgResDTO;
import com.ourhour.domain.org.service.OrgService;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.common.dto.PageResponse;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/{orgId}")
    public ResponseEntity<ApiResponse<OrgResDTO>> getOrgInfo(@PathVariable Long orgId) {
        OrgResDTO orgResDTO = orgService.getOrgInfo(orgId);

        return ResponseEntity.ok(ApiResponse.success(orgResDTO, "팀 조회에 성공하였습니다."));
    }

    // 회사 정보 수정
    @PutMapping("/{orgId}")
    public ResponseEntity<ApiResponse<OrgResDTO>> updateOrg(@PathVariable Long orgId,
            @Valid @RequestBody OrgReqDTO orgReqDTO) {
        OrgResDTO orgResDTO = orgService.updateOrg(orgId, orgReqDTO);

        return ResponseEntity.ok(ApiResponse.success(orgResDTO, "팀 수정에 성공하였습니다."));
    }

    // 구성원 목록 조회
    @GetMapping("/{orgId}/members")
    public ResponseEntity<ApiResponse<PageResponse<MemberInfoResDTO>>> getOrgMembers(
            @PathVariable @Min(value = 1, message = "팀 ID는 1 이상이어야 합니다.") Long orgId,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.") int currentPage,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.") @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.") int size) {

        Pageable pageable = PageRequest.of(currentPage, size);

        PageResponse<MemberInfoResDTO> response = orgService.getOrgMembers(orgId, pageable);

        return ResponseEntity.ok(ApiResponse.success(response, "팀 구성원 조회에 성공하였습니다."));
    }

    // 구성원 삭제
    @DeleteMapping("/{orgId}/members/{memberId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrgMember(@PathVariable Long orgId, @PathVariable Long memberId) {
        orgService.deleteOrgMember(orgId, memberId);

        return ResponseEntity.ok(ApiResponse.success(null, "팀 구성원 삭제에 성공하였습니다."));
    }

}
