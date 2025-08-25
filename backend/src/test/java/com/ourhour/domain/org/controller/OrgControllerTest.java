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
import org.springframework.http.ResponseEntity;

import com.ourhour.domain.org.dto.*;
import com.ourhour.domain.org.service.DepartmentService;
import com.ourhour.domain.org.service.OrgMemberService;
import com.ourhour.domain.org.service.OrgService;
import com.ourhour.domain.org.service.PositionService;
import com.ourhour.domain.project.dto.ProjectNameResDTO;
import com.ourhour.domain.member.dto.MemberInfoResDTO;
import com.ourhour.global.common.dto.ApiResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrgController 단위 테스트")
class OrgControllerTest {

    @Mock
    private OrgService orgService;

    @Mock
    private OrgMemberService orgMemberService;

    @Mock
    private DepartmentService departmentService;

    @Mock
    private PositionService positionService;

    @InjectMocks
    private OrgController orgController;

    private OrgReqDTO orgReqDTO;
    private OrgResDTO orgResDTO;
    private OrgDetailReqDTO orgDetailReqDTO;
    private OrgDetailResDTO orgDetailResDTO;
    private DepartmentReqDTO departmentReqDTO;
    private DepartmentResDTO departmentResDTO;
    private PositionReqDTO positionReqDTO;
    private PositionResDTO positionResDTO;
    private ProjectNameResDTO projectNameResDTO;
    private MemberInfoResDTO memberInfoResDTO;

    @BeforeEach
    void setUp() {
        orgReqDTO = new OrgReqDTO();
        orgReqDTO.setMemberName("테스트 사용자");
        orgReqDTO.setName("테스트 조직");
        orgReqDTO.setAddress("서울시 강남구");
        orgReqDTO.setEmail("test@example.com");
        orgReqDTO.setRepresentativeName("대표자");
        orgReqDTO.setPhone("02-1234-5678");
        orgReqDTO.setBusinessNumber("123-45-67890");
        orgReqDTO.setLogoImgUrl("https://example.com/logo.png");

        orgResDTO = OrgResDTO.builder()
                .orgId(1L)
                .name("테스트 조직")
                .address("서울시 강남구")
                .email("test@example.com")
                .representativeName("대표자")
                .phone("02-1234-5678")
                .businessNumber("123-45-67890")
                .logoImgUrl("https://example.com/logo.png")
                .memberName("테스트 사용자")
                .myRole("ROOT_ADMIN")
                .build();

        orgDetailReqDTO = new OrgDetailReqDTO();
        orgDetailReqDTO.setName("수정된 조직");
        orgDetailReqDTO.setAddress("서울시 서초구");
        orgDetailReqDTO.setEmail("updated@example.com");

        orgDetailResDTO = OrgDetailResDTO.builder()
                .orgId(1L)
                .name("수정된 조직")
                .address("서울시 서초구")
                .email("updated@example.com")
                .build();

        departmentReqDTO = new DepartmentReqDTO();
        departmentReqDTO.setName("개발팀");

        departmentResDTO = DepartmentResDTO.builder()
                .deptId(1L)
                .name("개발팀")
                .build();

        positionReqDTO = new PositionReqDTO();
        positionReqDTO.setName("팀장");

        positionResDTO = PositionResDTO.builder()
                .positionId(1L)
                .name("팀장")
                .build();

        projectNameResDTO = ProjectNameResDTO.builder()
                .projectId(1L)
                .name("테스트 프로젝트")
                .build();

        memberInfoResDTO = MemberInfoResDTO.builder()
                .memberId(1L)
                .name("테스트 멤버")
                .email("member@example.com")
                .build();
    }

    @Test
    @DisplayName("조직 등록 성공")
    void registerOrg_Success() {
        // given
        given(orgService.registerOrg(any(OrgReqDTO.class))).willReturn(orgResDTO);

        // when
        ResponseEntity<ApiResponse<OrgResDTO>> result = orgController.registerOrg(orgReqDTO);

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getData().getOrgId()).isEqualTo(1L);
        assertThat(result.getBody().getData().getName()).isEqualTo("테스트 조직");
        verify(orgService).registerOrg(orgReqDTO);
    }

    @Test
    @DisplayName("조직 정보 조회 성공")
    void getOrgInfo_Success() {
        // given
        given(orgService.getOrgInfo(eq(1L))).willReturn(orgDetailResDTO);

        // when
        ResponseEntity<ApiResponse<OrgDetailResDTO>> result = orgController.getOrgInfo(1L);

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getData().getOrgId()).isEqualTo(1L);
        assertThat(result.getBody().getData().getName()).isEqualTo("수정된 조직");
        verify(orgService).getOrgInfo(1L);
    }

    @Test
    @DisplayName("조직 정보 수정 성공")
    void updateOrg_Success() {
        // given
        given(orgService.updateOrg(eq(1L), any(OrgDetailReqDTO.class))).willReturn(orgDetailResDTO);

        // when
        ResponseEntity<ApiResponse<OrgDetailResDTO>> result = orgController.updateOrg(1L, orgDetailReqDTO);

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getData().getOrgId()).isEqualTo(1L);
        assertThat(result.getBody().getData().getName()).isEqualTo("수정된 조직");
        verify(orgService).updateOrg(1L, orgDetailReqDTO);
    }

    @Test
    @DisplayName("조직 삭제 성공")
    void deleteOrg_Success() {
        // when
        ResponseEntity<ApiResponse<Void>> result = orgController.deleteOrg(1L);

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getData()).isNull();
        verify(orgService).deleteOrg(1L);
    }

    @Test
    @DisplayName("내 프로젝트 목록 조회 성공")
    void getMyProjects_Success() {
        // given
        List<ProjectNameResDTO> projectList = List.of(projectNameResDTO);
        given(orgService.getMyProjects(any(), eq(1L))).willReturn(projectList);

        // when
        ResponseEntity<ApiResponse<List<ProjectNameResDTO>>> result = orgController.getMyProjects(1L);

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getData()).hasSize(1);
        assertThat(result.getBody().getData().get(0).getProjectId()).isEqualTo(1L);
        assertThat(result.getBody().getData().get(0).getName()).isEqualTo("테스트 프로젝트");
    }

    @Test
    @DisplayName("부서 생성 성공")
    void createDepartment_Success() {
        // given
        given(departmentService.createDepartment(eq(1L), any(DepartmentReqDTO.class))).willReturn(departmentResDTO);

        // when
        ResponseEntity<ApiResponse<DepartmentResDTO>> result = orgController.createDepartment(1L, departmentReqDTO);

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getData().getDeptId()).isEqualTo(1L);
        assertThat(result.getBody().getData().getName()).isEqualTo("개발팀");
        verify(departmentService).createDepartment(1L, departmentReqDTO);
    }

    @Test
    @DisplayName("부서 목록 조회 성공")
    void getDepartmentsByOrg_Success() {
        // given
        List<DepartmentResDTO> departmentList = List.of(departmentResDTO);
        given(departmentService.getDepartmentsByOrg(eq(1L))).willReturn(departmentList);

        // when
        ResponseEntity<ApiResponse<List<DepartmentResDTO>>> result = orgController.getDepartmentsByOrg(1L);

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getData()).hasSize(1);
        assertThat(result.getBody().getData().get(0).getDeptId()).isEqualTo(1L);
        assertThat(result.getBody().getData().get(0).getName()).isEqualTo("개발팀");
        verify(departmentService).getDepartmentsByOrg(1L);
    }

    @Test
    @DisplayName("부서 수정 성공")
    void updateDepartment_Success() {
        // given
        given(departmentService.updateDepartment(eq(1L), any(DepartmentReqDTO.class))).willReturn(departmentResDTO);

        // when
        ResponseEntity<ApiResponse<DepartmentResDTO>> result = orgController.updateDepartment(1L, 1L, departmentReqDTO);

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        verify(departmentService).updateDepartment(1L, departmentReqDTO);
    }

    @Test
    @DisplayName("부서 삭제 성공")
    void deleteDepartment_Success() {
        // when
        ResponseEntity<ApiResponse<Void>> result = orgController.deleteDepartment(1L, 1L);

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getData()).isNull();
        verify(departmentService).deleteDepartment(1L);
    }

    @Test
    @DisplayName("직책 생성 성공")
    void createPosition_Success() {
        // given
        given(positionService.createPosition(eq(1L), any(PositionReqDTO.class))).willReturn(positionResDTO);

        // when
        ResponseEntity<ApiResponse<PositionResDTO>> result = orgController.createPosition(1L, positionReqDTO);

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getData().getPositionId()).isEqualTo(1L);
        assertThat(result.getBody().getData().getName()).isEqualTo("팀장");
        verify(positionService).createPosition(1L, positionReqDTO);
    }

    @Test
    @DisplayName("직책 목록 조회 성공")
    void getPositionsByOrg_Success() {
        // given
        List<PositionResDTO> positionList = List.of(positionResDTO);
        given(positionService.getPositionsByOrg(eq(1L))).willReturn(positionList);

        // when
        ResponseEntity<ApiResponse<List<PositionResDTO>>> result = orgController.getPositionsByOrg(1L);

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getData()).hasSize(1);
        assertThat(result.getBody().getData().get(0).getPositionId()).isEqualTo(1L);
        assertThat(result.getBody().getData().get(0).getName()).isEqualTo("팀장");
        verify(positionService).getPositionsByOrg(1L);
    }

    @Test
    @DisplayName("직책 수정 성공")
    void updatePosition_Success() {
        // given
        given(positionService.updatePosition(eq(1L), any(PositionReqDTO.class))).willReturn(positionResDTO);

        // when
        ResponseEntity<ApiResponse<PositionResDTO>> result = orgController.updatePosition(1L, 1L, positionReqDTO);

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        verify(positionService).updatePosition(1L, positionReqDTO);
    }

    @Test
    @DisplayName("직책 삭제 성공")
    void deletePosition_Success() {
        // when
        ResponseEntity<ApiResponse<Void>> result = orgController.deletePosition(1L, 1L);

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getData()).isNull();
        verify(positionService).deletePosition(1L);
    }

    @Test
    @DisplayName("부서별 구성원 조회 성공")
    void getMembersByDepartment_Success() {
        // given
        List<MemberInfoResDTO> memberList = List.of(memberInfoResDTO);
        given(orgMemberService.getMembersByDepartment(eq(1L), eq(1L))).willReturn(memberList);

        // when
        ResponseEntity<ApiResponse<List<MemberInfoResDTO>>> result = orgController.getMembersByDepartment(1L, 1L);

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getData()).hasSize(1);
        assertThat(result.getBody().getData().get(0).getMemberId()).isEqualTo(1L);
        assertThat(result.getBody().getData().get(0).getName()).isEqualTo("테스트 멤버");
        verify(orgMemberService).getMembersByDepartment(1L, 1L);
    }

    @Test
    @DisplayName("직책별 구성원 조회 성공")
    void getMembersByPosition_Success() {
        // given
        List<MemberInfoResDTO> memberList = List.of(memberInfoResDTO);
        given(orgMemberService.getMembersByPosition(eq(1L), eq(1L))).willReturn(memberList);

        // when
        ResponseEntity<ApiResponse<List<MemberInfoResDTO>>> result = orgController.getMembersByPosition(1L, 1L);

        // then
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getData()).hasSize(1);
        assertThat(result.getBody().getData().get(0).getMemberId()).isEqualTo(1L);
        assertThat(result.getBody().getData().get(0).getName()).isEqualTo("테스트 멤버");
        verify(orgMemberService).getMembersByPosition(1L, 1L);
    }
}