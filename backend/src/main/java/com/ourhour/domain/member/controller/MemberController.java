package com.ourhour.domain.member.controller;

import com.ourhour.domain.member.dto.MemberOrgDetailResDTO;
import com.ourhour.domain.member.dto.MemberOrgSummaryResDTO;
import com.ourhour.domain.member.dto.MyMemberInfoReqDTO;
import com.ourhour.domain.member.dto.MyMemberInfoResDTO;
import com.ourhour.domain.member.exception.MemberException;
import com.ourhour.domain.member.service.MemberService;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.repository.OrgRepository;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.domain.org.exception.OrgException;
import com.ourhour.domain.auth.exception.AuthException;
import com.ourhour.global.jwt.annotation.OrgAuth;
import com.ourhour.global.jwt.annotation.OrgId;
import com.ourhour.global.jwt.util.UserContextHolder;
import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.common.dto.PageResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Validated
@Tag(name = "멤버", description = "멤버의 조직/개인 정보 API")
public class MemberController {

    private final MemberService memberService;
    private final OrgRepository orgRepository;

    // 본인이 속한 회사 목록 조회
    @GetMapping("/organizations")
    @Operation(summary = "내 조직 목록 조회", description = "현재 사용자 기준으로 참여 중인 조직 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<PageResponse<MemberOrgSummaryResDTO>>> findOrgListByUserId(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.") int currentPage,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.") @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.") int size) {

        Pageable pageable = PageRequest.of(currentPage - 1, size, Sort.by(Sort.Direction.ASC, "orgEntity.orgId"));

        Claims claims = UserContextHolder.get();

        if (claims == null) {
            throw AuthException.unauthorizedException();
        }

        Long userId = claims.getUserId();

        PageResponse<MemberOrgSummaryResDTO> response = memberService.findOrgSummaryByUserId(userId, pageable);

        return ResponseEntity.ok(ApiResponse.success(response, "현재 참여 중인 회사 목록 조회에 성공했습니다."));
    }

    // 본인이 속한 회사 상세 조회
    @OrgAuth
    @GetMapping("/organizations/{orgId}")
    @Operation(summary = "내 조직 상세 조회", description = "현재 사용자 기준 특정 조직의 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<MemberOrgDetailResDTO>> findOrgDetailByMemberIdAndOrgId(
            @OrgId @PathVariable Long orgId) {

        Claims claims = UserContextHolder.get();

        if (claims == null) {
            throw AuthException.unauthorizedException();
        }

        // 본인이 속하지 않은 회사 정보를 조회할 때
        Long memberId = claims.getOrgAuthorityList().stream()
                .filter(auth -> auth.getOrgId().equals(orgId))
                .map(auth -> auth.getMemberId())
                .findFirst()
                .orElseThrow(() -> MemberException.memberAccessDeniedException());

        // 삭제된 회사나 없는 회사를 조회할 때
        OrgEntity orgEntity = orgRepository.findById(orgId)
                .orElseThrow(() -> OrgException.orgNotFoundException());

        MemberOrgDetailResDTO memberOrgDetailResDTO = memberService.findOrgDetailByMemberIdAndOrgId(memberId,
                orgEntity.getOrgId());

        return ResponseEntity.ok(ApiResponse.success(memberOrgDetailResDTO, "본인이 속한 회사 상세 조회에 성공했습니다."));
    }

    // 회사 내 개인 정보 조회
    @OrgAuth
    @GetMapping("/organizations/{orgId}/me")
    @Operation(summary = "회사 내 내 정보 조회", description = "특정 조직 내에서의 내 개인 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<MyMemberInfoResDTO>> findMyMemberInfoInOrg(@OrgId @PathVariable Long orgId) {

        MyMemberInfoResDTO memberInfoResDTO = memberService.findMyMemberInfoInOrg(orgId);

        ApiResponse<MyMemberInfoResDTO> apiResponse = ApiResponse.success(memberInfoResDTO, "회사 내 개인 정보 조회에 성공하였습니다.");

        return ResponseEntity.ok(apiResponse);

    }

    // 회사 내 개인 정보 수정
    @OrgAuth
    @PutMapping("/organizations/{orgId}/me")
    @Operation(summary = "회사 내 내 정보 수정", description = "특정 조직 내에서의 내 개인 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<MyMemberInfoResDTO>> updateMyMemberInfoInOrg(@OrgId @PathVariable Long orgId,
            @RequestBody MyMemberInfoReqDTO myMemberInfoReqDTO) {

        MyMemberInfoResDTO memberInfoResDTO = memberService.updateMyMemberInfoInOrg(orgId, myMemberInfoReqDTO);

        ApiResponse<MyMemberInfoResDTO> apiResponse = ApiResponse.success(memberInfoResDTO, "회사 내 개인 정보 수정에 성공하였습니다.");

        return ResponseEntity.ok(apiResponse);

    }

}
