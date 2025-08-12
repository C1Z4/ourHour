package com.ourhour.domain.org.controller;

import com.ourhour.domain.org.dto.OrgInvReqDTO;
import com.ourhour.domain.org.dto.OrgInvResDTO;
import com.ourhour.domain.org.dto.OrgJoinReqDTO;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.org.service.OrgInvService;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.jwt.annotation.OrgAuth;
import com.ourhour.global.jwt.annotation.OrgId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/organizations")
@Tag(name = "조직 초대", description = "조직 초대/검증/수락 API")
public class OrgInvController {

    private final OrgInvService orgInvService;

    @OrgAuth(accessLevel = Role.ADMIN)
    @PostMapping("/{orgId}/invitation")
    @Operation(summary = "초대 메일 발송", description = "조직 구성원 초대 메일을 발송합니다.")
    public ResponseEntity<ApiResponse<Void>> sendInvLink(@OrgId @PathVariable Long orgId, @RequestBody List<OrgInvReqDTO> orgInvReqDTOList) {

        orgInvService.sendInvLink(orgId, orgInvReqDTOList);

        ApiResponse<Void> apiResponse = ApiResponse.success(null, "초대 메일이 발송되었습니다.");

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/invitation/verify")
    @Operation(summary = "초대 메일 인증", description = "초대 메일의 토큰을 검증합니다.")
    public ResponseEntity<ApiResponse<Void>> verifyInvEmail(@RequestParam String token) {

        orgInvService.verifyInvEmail(token);

        ApiResponse<Void> apiResponse = ApiResponse.success(null, "팀 참여를 위한 이메일 인증에 성공하였습니다.");

        return ResponseEntity.ok(apiResponse);

    }

    @PostMapping("/invitation/accept")
    @Operation(summary = "초대 수락", description = "초대 수락 후 조직 참여를 완료합니다.")
    public ResponseEntity<ApiResponse<Void>> acceptInv(@RequestBody OrgJoinReqDTO orgJoinReqDTO) {

        orgInvService.acceptInvEmail(orgJoinReqDTO);

        ApiResponse<Void> apiResponse = ApiResponse.success(null, "팀 참여가 완료되었습니다.");

        return ResponseEntity.ok(apiResponse);

    }

    @OrgAuth(accessLevel = Role.ADMIN)
    @GetMapping("/{orgId}/invitations")
    @Operation(summary = "발송한 초대 목록 조회", description = "조직에서 발송한 초대 링크 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<OrgInvResDTO>>> getInvList(@OrgId @PathVariable Long orgId) {

        List<OrgInvResDTO> orgInvResDTOList = orgInvService.getInvList(orgId);

        ApiResponse<List<OrgInvResDTO>> apiResponse = ApiResponse.success(orgInvResDTOList,
                "초대 링크를 보낸 리스트 조회에 성공하였습니다.");

        return ResponseEntity.ok(apiResponse);

    }

}
