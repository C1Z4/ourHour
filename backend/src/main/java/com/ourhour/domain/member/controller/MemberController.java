package com.ourhour.domain.member.controller;

import com.ourhour.domain.member.dto.MemberOrgDetailResDTO;
import com.ourhour.domain.member.dto.MemberOrgSummaryResDTO;
import com.ourhour.domain.member.service.MemberService;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.repository.OrgRepository;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.exception.BusinessException;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
                        @RequestParam(defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.") @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.") int size   
    ) {

        Pageable pageable = PageRequest.of(currentPage - 1, size, Sort.by(Sort.Direction.ASC, "orgEntity.orgId"));

        Claims claims = UserContextHolder.get();    

        if (claims == null) {
            throw BusinessException.unauthorized("인증 정보가 없습니다.");
        }

        Long memberId = claims.getOrgAuthorityList().stream()
                                .map(auth -> auth.getMemberId())
                                .findFirst()
                                .orElseThrow(() -> BusinessException.unauthorized("멤버 정보를 찾을 수 없습니다."));

        PageResponse<MemberOrgSummaryResDTO> response = memberService.findOrgSummaryByMemberId(memberId, pageable);

        return ResponseEntity.ok(ApiResponse.success(response, "현재 참여 중인 회사 목록 조회에 성공했습니다."));
    }

    // 본인이 속한 회사 상세 조회
    @GetMapping("/organizations/{orgId}")
    public ResponseEntity<ApiResponse<MemberOrgDetailResDTO>> findOrgDetailByMemberIdAndOrgId(@PathVariable Long orgId) {

        Claims claims = UserContextHolder.get();

        if (claims == null) {
            throw BusinessException.unauthorized("인증 정보가 없습니다.");
        }

        // 본인이 속하지 않은 회사 정보를 조회할 때
        Long memberId = claims.getOrgAuthorityList().stream()
                                .filter(auth -> auth.getOrgId().equals(orgId))
                                .map(auth -> auth.getMemberId())
                                .findFirst()
                                .orElseThrow(() -> BusinessException.forbidden("해당 회사의 멤버가 아닙니다."));
                            
        // 삭제된 회사나 없는 회사를 조회할 때
        OrgEntity orgEntity = orgRepository.findById(orgId)
                .orElseThrow(() -> BusinessException.badRequest("존재하지 않는 회사입니다."));

        MemberOrgDetailResDTO memberOrgDetailResDTO = memberService.findOrgDetailByMemberIdAndOrgId(memberId, orgEntity.getOrgId());

        return ResponseEntity.ok(ApiResponse.success(memberOrgDetailResDTO, "본인이 속한 회사 상세 조회에 성공했습니다."));
    }


}
