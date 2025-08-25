package com.ourhour.domain.org.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.ourhour.domain.member.dto.MemberInfoResDTO;
import com.ourhour.domain.org.dto.OrgMemberRoleReqDTO;
import com.ourhour.domain.org.dto.OrgMemberRoleResDTO;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.org.service.OrgMemberService;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.common.dto.PageResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrgMemberController 단위 테스트")
class OrgMemberControllerTest {

    @Mock
    private OrgMemberService orgMemberService;

    @InjectMocks
    private OrgMemberController orgMemberController;

    private MemberInfoResDTO memberInfoResDTO;
    private OrgMemberRoleReqDTO orgMemberRoleReqDTO;
    private OrgMemberRoleResDTO orgMemberRoleResDTO;
    private PageResponse<MemberInfoResDTO> pageResponse;

    @BeforeEach
    void setUp() {
        memberInfoResDTO = MemberInfoResDTO.builder()
                .memberId(1L)
                .name("테스트 멤버")
                .email("member@example.com")
                .profileImgUrl("https://example.com/profile.jpg")
                .role("MEMBER")
                .deptName("개발팀")
                .positionName("팀장")
                .build();

        orgMemberRoleReqDTO = new OrgMemberRoleReqDTO();
        orgMemberRoleReqDTO.setRole(Role.ADMIN);

        orgMemberRoleResDTO = OrgMemberRoleResDTO.builder()
                .memberId(1L)
                .role(Role.ADMIN)
                .build();

        List<MemberInfoResDTO> memberList = List.of(memberInfoResDTO);
        pageResponse = PageResponse.<MemberInfoResDTO>builder()
                .data(memberList)
                .currentPage(1)
                .totalPages(1)
                .totalElements(1L)
                .size(10)
                .hasNext(false)
                .hasPrevious(false)
                .build();
    }

    @Test
    @DisplayName("구성원 목록 조회 성공")
    void getOrgMembers_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        given(orgMemberService.getOrgMembers(eq(1L), eq(""), eq(pageable))).willReturn(pageResponse);

        // when
        ResponseEntity<ApiResponse<PageResponse<MemberInfoResDTO>>> result = 
                orgMemberController.getOrgMembers(1L, 1, 10, "");

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getData().getData()).hasSize(1);
        assertThat(result.getBody().getData().getData().get(0).getMemberId()).isEqualTo(1L);
        assertThat(result.getBody().getData().getData().get(0).getName()).isEqualTo("테스트 멤버");
        assertThat(result.getBody().getData().getCurrentPage()).isEqualTo(1);
        assertThat(result.getBody().getData().getTotalElements()).isEqualTo(1L);
        verify(orgMemberService).getOrgMembers(1L, "", pageable);
    }

    @Test
    @DisplayName("구성원 목록 조회 시 검색어 포함")
    void getOrgMembers_WithSearch() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        given(orgMemberService.getOrgMembers(eq(1L), eq("테스트"), eq(pageable))).willReturn(pageResponse);

        // when
        ResponseEntity<ApiResponse<PageResponse<MemberInfoResDTO>>> result = 
                orgMemberController.getOrgMembers(1L, 1, 10, "테스트");

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getMessage()).isEqualTo("팀 구성원 조회에 성공하였습니다.");
        verify(orgMemberService).getOrgMembers(1L, "테스트", pageable);
    }

    @Test
    @DisplayName("전체 구성원 목록 조회 성공")
    void getAllOrgMembers_Success() {
        // given
        List<MemberInfoResDTO> memberList = List.of(memberInfoResDTO);
        given(orgMemberService.getAllOrgMembers(eq(1L))).willReturn(memberList);

        // when
        ResponseEntity<ApiResponse<List<MemberInfoResDTO>>> result = 
                orgMemberController.getAllOrgMembers(1L);

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getData()).hasSize(1);
        assertThat(result.getBody().getData().get(0).getMemberId()).isEqualTo(1L);
        assertThat(result.getBody().getData().get(0).getName()).isEqualTo("테스트 멤버");
        assertThat(result.getBody().getData().get(0).getEmail()).isEqualTo("member@example.com");
        verify(orgMemberService).getAllOrgMembers(1L);
    }

    @Test
    @DisplayName("구성원 상세 조회 성공")
    void getOrgMember_Success() {
        // given
        given(orgMemberService.getOrgMember(eq(1L), eq(1L))).willReturn(memberInfoResDTO);

        // when
        ResponseEntity<ApiResponse<MemberInfoResDTO>> result = 
                orgMemberController.getOrgMember(1L, 1L);

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getMessage()).isEqualTo("구성원 상세 조회에 성공하셨습니다.");
        assertThat(result.getBody().getData().getMemberId()).isEqualTo(1L);
        assertThat(result.getBody().getData().getName()).isEqualTo("테스트 멤버");
        assertThat(result.getBody().getData().getEmail()).isEqualTo("member@example.com");
        assertThat(result.getBody().getData().getRole()).isEqualTo("MEMBER");
        verify(orgMemberService).getOrgMember(1L, 1L);
    }

    @Test
    @DisplayName("구성원 권한 변경 성공")
    void changeOrgMemberRole_Success() {
        // given
        given(orgMemberService.changeRole(eq(1L), eq(1L), any(OrgMemberRoleReqDTO.class)))
                .willReturn(orgMemberRoleResDTO);

        // when
        ResponseEntity<ApiResponse<OrgMemberRoleResDTO>> result = 
                orgMemberController.getOrgMember(1L, 1L, orgMemberRoleReqDTO);

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getMessage()).isEqualTo("구성원 변경 권한에 성공하셨습니다.");
        assertThat(result.getBody().getData().getMemberId()).isEqualTo(1L);
        assertThat(result.getBody().getData().getRole()).isEqualTo(Role.ADMIN);
        verify(orgMemberService).changeRole(1L, 1L, orgMemberRoleReqDTO);
    }

    @Test
    @DisplayName("구성원 삭제 성공")
    void deleteOrgMember_Success() {
        // when
        ResponseEntity<ApiResponse<Void>> result = 
                orgMemberController.deleteOrgMember(1L, 1L);

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getMessage()).isEqualTo("팀 구성원 삭제에 성공하였습니다.");
        assertThat(result.getBody().getData()).isNull();
        verify(orgMemberService).deleteOrgMember(1L, 1L);
    }

    @Test
    @DisplayName("조직 나가기 성공")
    void exitOrg_Success() {
        // when
        ResponseEntity<ApiResponse<Void>> result = 
                orgMemberController.exitOrg(1L);

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getMessage()).isEqualTo("회사 나가기에 성공하였습니다.");
        assertThat(result.getBody().getData()).isNull();
        verify(orgMemberService).exitOrg(1L);
    }
}
