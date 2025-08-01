package com.ourhour.domain.member.controller;

import com.ourhour.domain.member.dto.MemberOrgDetailResDTO;
import com.ourhour.domain.member.dto.MemberOrgSummaryResDTO;
import com.ourhour.domain.member.dto.MyMemberInfoReqDTO;
import com.ourhour.domain.member.dto.MyMemberInfoResDTO;
import com.ourhour.domain.member.exception.MemberException;
import com.ourhour.domain.member.exception.MemberOrgException;
import com.ourhour.domain.member.service.MemberService;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.repository.OrgRepository;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.domain.auth.exception.AuthException;
import com.ourhour.global.exception.BusinessException;
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

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Validated
public class MemberController {

    private final MemberService memberService;
    private final OrgRepository orgRepository;

    // 본인이 속한 회사 목록 조회
    @GetMapping("/organizations")
    public ResponseEntity<ApiResponse<PageResponse<MemberOrgSummaryResDTO>>> findOrgListByMemberId(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.") int currentPage,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.") @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.") int size) {

        Pageable pageable = PageRequest.of(currentPage - 1, size, Sort.by(Sort.Direction.ASC, "orgEntity.orgId"));

        Claims claims = UserContextHolder.get();

        if (claims == null) {
            throw AuthException.unauthorizedException();
        }

        List<Long> memberIds = claims.getOrgAuthorityList().stream()
                .map(auth -> auth.getMemberId())
                .collect(Collectors.toList());

        if (memberIds.isEmpty()) {
            throw MemberException.memberNotFoundException();
        }

        PageResponse<MemberOrgSummaryResDTO> response = memberService.findOrgSummaryByMemberIds(memberIds, pageable);

        return ResponseEntity.ok(ApiResponse.success(response, "현재 참여 중인 회사 목록 조회에 성공했습니다."));
    }

    // 본인이 속한 회사 상세 조회
    @OrgAuth
    @GetMapping("/organizations/{orgId}")
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
                .orElseThrow(() -> MemberOrgException.orgNotFoundException());

        MemberOrgDetailResDTO memberOrgDetailResDTO = memberService.findOrgDetailByMemberIdAndOrgId(memberId,
                orgEntity.getOrgId());

        return ResponseEntity.ok(ApiResponse.success(memberOrgDetailResDTO, "본인이 속한 회사 상세 조회에 성공했습니다."));
    }

    // 회사 내 개인 정보 조회
    @OrgAuth
    @GetMapping("/organizations/{orgId}/me")
    public ResponseEntity<ApiResponse<MyMemberInfoResDTO>> findMyMemberInfoInOrg(@OrgId @PathVariable Long orgId) {

        MyMemberInfoResDTO memberInfoResDTO = memberService.findMyMemberInfoInOrg(orgId);

        ApiResponse<MyMemberInfoResDTO> apiResponse = ApiResponse.success(memberInfoResDTO, "회사 내 개인 정보 조회에 성공하였습니다.");

        return ResponseEntity.ok(apiResponse);

    }

    // 회사 내 개인 정보 수정
    @OrgAuth
    @PutMapping("/organizations/{orgId}/me")
    public ResponseEntity<ApiResponse<MyMemberInfoResDTO>> updateMyMemberInfoInOrg(@OrgId @PathVariable Long orgId,
            @RequestBody MyMemberInfoReqDTO myMemberInfoReqDTO) {

        MyMemberInfoResDTO memberInfoResDTO = memberService.updateMyMemberInfoInOrg(orgId, myMemberInfoReqDTO);

        ApiResponse<MyMemberInfoResDTO> apiResponse = ApiResponse.success(memberInfoResDTO, "회사 내 개인 정보 수정에 성공하였습니다.");

        return ResponseEntity.ok(apiResponse);

    }

}
