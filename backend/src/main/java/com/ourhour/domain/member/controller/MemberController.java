package com.ourhour.domain.member.controller;

import com.ourhour.domain.member.dto.MemberOrgSummaryResDTO;
import com.ourhour.domain.member.sevice.MemberService;
import com.ourhour.global.common.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/me/organizations")
    public ResponseEntity<ApiResponse<List<MemberOrgSummaryResDTO>>> findOrgListByMemberId(@RequestParam(value = "testMemberId", required = false) Long testMemberId) {
        List<MemberOrgSummaryResDTO> memberOrgSummaryResDTOList = memberService.findOrgListByMemberId(testMemberId);

        if (memberOrgSummaryResDTOList.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success(memberOrgSummaryResDTOList, "현재 소속된 팀이 없습니다."));
        } else {
            return ResponseEntity.ok(ApiResponse.success(memberOrgSummaryResDTOList, "현재 참여 중인 팀 목록 조회에 성공했습니다."));
        }
    }


}
