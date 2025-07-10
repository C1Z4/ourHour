package com.ourhour.domain.member.controller;

import com.ourhour.domain.member.dto.MemberOrgDetailResDTO;
import com.ourhour.domain.member.dto.MemberOrgSummaryResDTO;
import com.ourhour.domain.member.service.MemberService;
import com.ourhour.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me/organizations")
    // [임시] JWT 인증 미구현으로 테스트용 memberId 받음 (반드시 추후 제거)
    public ResponseEntity<ApiResponse<List<MemberOrgSummaryResDTO>>> findOrgListByMemberId(@RequestParam(value = "testMemberId", required = false) Long testMemberId) {
        List<MemberOrgSummaryResDTO> memberOrgSummaryResDTOList = memberService.findOrgListByMemberId(testMemberId);

        if (memberOrgSummaryResDTOList.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success(memberOrgSummaryResDTOList, "현재 소속된 팀이 없습니다."));
        } else {
            return ResponseEntity.ok(ApiResponse.success(memberOrgSummaryResDTOList, "현재 참여 중인 팀 목록 조회에 성공했습니다."));
        }
    }

    @GetMapping("/me/organizations/{orgId}")
    // [임시] JWT 인증 미구현으로 테스트용 memberId 받음 (반드시 추후 제거)
    public ResponseEntity<ApiResponse<MemberOrgDetailResDTO>> findOrgDetailByMemberIdAndOrgId(@RequestParam(value = "testMemberId", required = false) Long testMemberId, @PathVariable Long orgId) {
        // 본인이 속하지 않은 회사 정보를 조회할 때
        // 삭제된 회사나 없는 회사를 조회할 때

        MemberOrgDetailResDTO memberOrgDetailResDTO = memberService.findOrgDetailByMemberIdAndOrgId(testMemberId, orgId);

        return ResponseEntity.ok(ApiResponse.success(memberOrgDetailResDTO, "회사 상세 조회에 성공했습니다."));
    }


}
