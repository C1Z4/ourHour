package com.ourhour.domain.project.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.project.dto.IssueDetailDTO;
import com.ourhour.domain.project.dto.IssueReqDTO;
import com.ourhour.domain.project.dto.IssueStatusReqDTO;
import com.ourhour.domain.project.dto.IssueSummaryDTO;
import com.ourhour.domain.project.dto.IssueTagDTO;
import com.ourhour.domain.project.dto.MileStoneInfoDTO;
import com.ourhour.domain.project.dto.MilestoneReqDTO;
import com.ourhour.domain.project.dto.ProjecUpdateReqDTO;
import com.ourhour.domain.project.dto.ProjectInfoDTO;
import com.ourhour.domain.project.dto.ProjectParticipantDTO;
import com.ourhour.domain.project.dto.ProjectReqDTO;
import com.ourhour.domain.project.dto.ProjectSummaryResDTO;
import com.ourhour.domain.project.entity.ProjectEntity;
import com.ourhour.domain.project.repository.ProjectRepository;
import com.ourhour.domain.org.repository.OrgRepository;
import com.ourhour.global.common.dto.PageResponse;
import com.ourhour.domain.project.service.IssueService;
import com.ourhour.domain.project.service.MilestoneService;
import com.ourhour.domain.project.service.ProjectParticipantService;
import com.ourhour.domain.project.service.ProjectService;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.jwt.dto.CustomUserDetails;
import com.ourhour.global.jwt.dto.OrgAuthority;
import com.ourhour.domain.project.mapper.ProjectMapper;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectController 테스트")
class ProjectControllerTest {

        @Mock
        private ProjectService projectService;

        @Mock
        private ProjectParticipantService projectParticipantService;

        @Mock
        private IssueService issueService;

        @Mock
        private MilestoneService milestoneService;

        @Mock
        private ProjectRepository projectRepository;

        @Mock
        private OrgRepository orgRepository;

        @Mock
        private ProjectMapper projectMapper;

        @InjectMocks
        private ProjectController projectController;

        private MockMvc mockMvc;
        private ObjectMapper objectMapper;

        private static final Long USER_ID = 1L;
        private static final Long ORG_ID = 1L;
        private static final Long MEMBER_ID = 1L;
        private static final Long PROJECT_ID = 1L;
        private static final Long MILESTONE_ID = 1L;
        private static final Long ISSUE_ID = 1L;
        private static final Long ISSUE_TAG_ID = 1L;

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
                objectMapper = new ObjectMapper();
        }

        private void setAuthenticationContext() {
                OrgAuthority orgAuthority = new OrgAuthority(ORG_ID, MEMBER_ID, Role.ADMIN);
                Claims claims = new Claims(USER_ID, "test@example.com", List.of(orgAuthority));

                CustomUserDetails mockUser = new CustomUserDetails(
                                USER_ID,
                                claims.getEmail(),
                                "password",
                                List.of(orgAuthority),
                                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

                Authentication auth = new UsernamePasswordAuthenticationToken(
                                mockUser,
                                null,
                                mockUser.getAuthorities());

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(auth);
                SecurityContextHolder.setContext(context);
        }

        @Test
        @DisplayName("프로젝트 등록 - 성공")
        void createProject_Success() throws Exception {
                // Given
                setAuthenticationContext();
                ProjectReqDTO projectReqDTO = new ProjectReqDTO();
                projectReqDTO.setName("테스트 프로젝트");
                projectReqDTO.setDescription("테스트 프로젝트 설명");

                given(projectService.createProject(eq(ORG_ID), any(ProjectReqDTO.class)))
                                .willReturn(ApiResponse.success(null, "프로젝트 등록이 완료되었습니다."));

                // When & Then
                mockMvc.perform(post("/api/organizations/{orgId}/projects", ORG_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projectReqDTO)))
                                .andExpect(status().isOk());

                then(projectService).should().createProject(eq(ORG_ID), any(ProjectReqDTO.class));

                SecurityContextHolder.clearContext();
        }

        @Test
        @DisplayName("프로젝트 수정 - 성공")
        void updateProject_Success() throws Exception {
                // Given
                setAuthenticationContext();
                ProjecUpdateReqDTO updateReqDTO = new ProjecUpdateReqDTO();
                updateReqDTO.setName("수정된 프로젝트");
                updateReqDTO.setDescription("수정된 프로젝트 설명");

                given(projectService.updateProject(eq(PROJECT_ID), any(ProjecUpdateReqDTO.class)))
                                .willReturn(ApiResponse.success(null, "프로젝트 수정이 완료되었습니다."));

                // When & Then
                mockMvc.perform(put("/api/organizations/{orgId}/projects/{projectId}", ORG_ID, PROJECT_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateReqDTO)))
                                .andExpect(status().isOk());

                then(projectService).should().updateProject(eq(PROJECT_ID), any(ProjecUpdateReqDTO.class));

                SecurityContextHolder.clearContext();
        }

        @Test
        @DisplayName("프로젝트 삭제 - 성공")
        void deleteProject_Success() throws Exception {
                // Given
                setAuthenticationContext();
                given(projectService.deleteProject(PROJECT_ID))
                                .willReturn(ApiResponse.success(null, "프로젝트 삭제가 완료되었습니다."));

                // When & Then
                mockMvc.perform(delete("/api/organizations/{orgId}/projects/{projectId}", ORG_ID, PROJECT_ID))
                                .andExpect(status().isOk());

                then(projectService).should().deleteProject(PROJECT_ID);

                SecurityContextHolder.clearContext();
        }

        @Test
        @DisplayName("프로젝트 정보 조회 - 성공")
        void getProjectInfo_Success() throws Exception {
                // Given
                ProjectInfoDTO projectInfo = new ProjectInfoDTO();
                projectInfo.setProjectId(PROJECT_ID);
                projectInfo.setName("테스트 프로젝트");
                projectInfo.setDescription("테스트 프로젝트 설명");

                given(projectService.getProjectInfo(PROJECT_ID))
                                .willReturn(ApiResponse.success(projectInfo, "프로젝트 정보 조회에 성공했습니다."));

                // When & Then
                mockMvc.perform(get("/api/organizations/{orgId}/projects/{projectId}/info", ORG_ID, PROJECT_ID))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.projectId").value(PROJECT_ID))
                                .andExpect(jsonPath("$.data.name").value("테스트 프로젝트"));

                then(projectService).should().getProjectInfo(PROJECT_ID);
        }

        @Test
        @DisplayName("마일스톤 등록 - 성공")
        void createMilestone_Success() throws Exception {
                // Given
                MilestoneReqDTO milestoneReqDTO = new MilestoneReqDTO();
                milestoneReqDTO.setName("새 마일스톤");

                given(milestoneService.createMilestone(eq(PROJECT_ID), any(MilestoneReqDTO.class)))
                                .willReturn(ApiResponse.success(null, "마일스톤 등록이 완료되었습니다."));

                // When - Direct controller call to bypass aspects
                ResponseEntity<ApiResponse<Void>> response = projectController.createMilestone(ORG_ID, PROJECT_ID,
                                milestoneReqDTO);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getBody()).isNotNull();
                then(milestoneService).should().createMilestone(eq(PROJECT_ID), any(MilestoneReqDTO.class));
        }

        @Test
        @DisplayName("마일스톤 삭제 - 성공")
        void deleteMilestone_Success() throws Exception {
                // Given
                given(milestoneService.deleteMilestone(ORG_ID, MILESTONE_ID))
                                .willReturn(ApiResponse.success(null, "마일스톤 삭제가 완료되었습니다."));

                // When & Then
                mockMvc.perform(delete("/api/organizations/{orgId}/projects/{projectId}/milestones/{milestoneId}", ORG_ID, PROJECT_ID, MILESTONE_ID))
                                .andExpect(status().isOk());

                then(milestoneService).should().deleteMilestone(ORG_ID, MILESTONE_ID);
        }

        @Test
        @DisplayName("프로젝트 요약 목록 조회 - 성공")
        void getProjectsSummaryList_Success() throws Exception {
                // Given
                int currentPage = 1;
                int size = 10;
                int participantLimit = 3;
                boolean myProjectsOnly = false;

                ProjectSummaryResDTO projectSummary = new ProjectSummaryResDTO();
                PageResponse<ProjectSummaryResDTO> pageResponse = PageResponse.of(
                                new PageImpl<>(List.of(projectSummary), PageRequest.of(currentPage - 1, size), 1));

                given(projectService.getProjectsSummaryList(eq(ORG_ID), eq(participantLimit), eq(myProjectsOnly),
                                any(Pageable.class)))
                                .willReturn(ApiResponse.success(pageResponse, "프로젝트 요약 목록을 조회했습니다."));

                // When & Then
                mockMvc.perform(get("/api/organizations/{orgId}/projects", ORG_ID)
                                .param("currentPage", String.valueOf(currentPage))
                                .param("size", String.valueOf(size))
                                .param("participantLimit", String.valueOf(participantLimit))
                                .param("myProjectsOnly", String.valueOf(myProjectsOnly)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("OK"))
                                .andExpect(jsonPath("$.message").value("프로젝트 요약 목록을 조회했습니다."));

                then(projectService).should().getProjectsSummaryList(eq(ORG_ID), eq(participantLimit),
                                eq(myProjectsOnly), any(Pageable.class));
        }

        @Test
        @DisplayName("프로젝트 참가자 목록 조회 - 성공")
        void getProjectParticipants_Success() throws Exception {
                // Given
                int currentPage = 1;
                int size = 10;
                String search = "test";

                ProjectParticipantDTO participant = new ProjectParticipantDTO();
                PageResponse<ProjectParticipantDTO> pageResponse = PageResponse.of(
                                new PageImpl<>(List.of(participant), PageRequest.of(currentPage - 1, size), 1));

                given(projectParticipantService.getProjectParticipants(eq(PROJECT_ID), eq(ORG_ID), eq(search),
                                any(Pageable.class)))
                                .willReturn(ApiResponse.success(pageResponse, "프로젝트 참가자 목록을 조회했습니다."));

                // When & Then
                mockMvc.perform(get("/api/organizations/{orgId}/projects/{projectId}/participants", ORG_ID, PROJECT_ID)
                                .param("currentPage", String.valueOf(currentPage))
                                .param("size", String.valueOf(size))
                                .param("search", search))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("OK"))
                                .andExpect(jsonPath("$.message").value("프로젝트 참가자 목록을 조회했습니다."));

                then(projectParticipantService).should().getProjectParticipants(eq(PROJECT_ID), eq(ORG_ID), eq(search),
                                any(Pageable.class));
        }

        @Test
        @DisplayName("프로젝트 참가자 삭제 - 성공")
        void deleteProjectParticipant_Success() throws Exception {
                // Given
                setAuthenticationContext();
                given(projectParticipantService.deleteProjectParticipant(PROJECT_ID, MEMBER_ID))
                                .willReturn(ApiResponse.success(null, "프로젝트 참가자가 삭제되었습니다."));

                // When & Then
                mockMvc.perform(delete("/api/organizations/{orgId}/projects/{projectId}/participants/{memberId}", ORG_ID, PROJECT_ID,
                                MEMBER_ID))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("OK"))
                                .andExpect(jsonPath("$.message").value("프로젝트 참가자가 삭제되었습니다."));

                then(projectParticipantService).should().deleteProjectParticipant(PROJECT_ID, MEMBER_ID);
                SecurityContextHolder.clearContext();
        }

        @Test
        @DisplayName("프로젝트 마일스톤 목록 조회 - 성공")
        void getProjectMilestones_Success() throws Exception {
                // Given
                boolean myMilestonesOnly = false;
                int currentPage = 1;
                int size = 10;

                MileStoneInfoDTO milestone = new MileStoneInfoDTO();
                PageResponse<MileStoneInfoDTO> pageResponse = PageResponse.of(
                                new PageImpl<>(List.of(milestone), PageRequest.of(currentPage - 1, size), 1));

                given(projectService.getProjectMilestones(eq(ORG_ID), eq(PROJECT_ID), eq(myMilestonesOnly), any(Pageable.class)))
                                .willReturn(ApiResponse.success(pageResponse, "마일스톤 목록을 조회했습니다."));

                // When & Then
                mockMvc.perform(get("/api/organizations/{orgId}/projects/{projectId}/milestones", ORG_ID, PROJECT_ID)
                                .param("myMilestonesOnly", String.valueOf(myMilestonesOnly))
                                .param("currentPage", String.valueOf(currentPage))
                                .param("size", String.valueOf(size)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("OK"))
                                .andExpect(jsonPath("$.message").value("마일스톤 목록을 조회했습니다."));

                then(projectService).should().getProjectMilestones(eq(ORG_ID), eq(PROJECT_ID), eq(myMilestonesOnly),
                                any(Pageable.class));
        }

        @Test
        @DisplayName("마일스톤 이슈 목록 조회 - 성공")
        void getMilestoneIssues_Success() throws Exception {
                // Given
                Long milestoneId = 1L;
                boolean myIssuesOnly = false;
                int currentPage = 1;
                int size = 10;

                IssueSummaryDTO issueSummary = new IssueSummaryDTO();
                PageResponse<IssueSummaryDTO> pageResponse = PageResponse.of(
                                new PageImpl<>(List.of(issueSummary), PageRequest.of(currentPage - 1, size), 1));

                given(issueService.getMilestoneIssues(eq(PROJECT_ID), eq(milestoneId), eq(myIssuesOnly),
                                any(Pageable.class)))
                                .willReturn(ApiResponse.success(pageResponse, "이슈 목록을 조회했습니다."));

                // When & Then
                mockMvc.perform(get("/api/organizations/{orgId}/projects/{projectId}/issues", ORG_ID, PROJECT_ID)
                                .param("milestoneId", String.valueOf(milestoneId))
                                .param("myIssuesOnly", String.valueOf(myIssuesOnly))
                                .param("currentPage", String.valueOf(currentPage))
                                .param("size", String.valueOf(size)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("OK"))
                                .andExpect(jsonPath("$.message").value("이슈 목록을 조회했습니다."));

                then(issueService).should().getMilestoneIssues(eq(PROJECT_ID), eq(milestoneId), eq(myIssuesOnly),
                                any(Pageable.class));
        }

        @Test
        @DisplayName("마일스톤 수정 - 성공")
        void updateMilestone_Success() throws Exception {
                // Given
                MilestoneReqDTO milestoneReqDTO = new MilestoneReqDTO();
                milestoneReqDTO.setName("수정된 마일스톤");

                given(milestoneService.updateMilestone(MILESTONE_ID, milestoneReqDTO))
                                .willReturn(ApiResponse.success(null, "마일스톤이 수정되었습니다."));

                // When - Direct controller call to bypass aspects
                ResponseEntity<ApiResponse<Void>> response = projectController.updateMilestone(ORG_ID, PROJECT_ID, MILESTONE_ID,
                                milestoneReqDTO);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getBody()).isNotNull();
                then(milestoneService).should().updateMilestone(MILESTONE_ID, milestoneReqDTO);
        }

        @Test
        @DisplayName("이슈 상세 조회 - 성공")
        void getIssueInfo_Success() throws Exception {
                // Given
                IssueDetailDTO issueDetail = new IssueDetailDTO();

                given(issueService.getIssueDetail(ISSUE_ID))
                                .willReturn(ApiResponse.success(issueDetail, "이슈 상세 정보를 조회했습니다."));

                // When & Then
                mockMvc.perform(get("/api/organizations/{orgId}/projects/{projectId}/issues/{issueId}", ORG_ID, PROJECT_ID, ISSUE_ID))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("OK"))
                                .andExpect(jsonPath("$.message").value("이슈 상세 정보를 조회했습니다."));

                then(issueService).should().getIssueDetail(ISSUE_ID);
        }

        @Test
        @DisplayName("이슈 생성 - 성공")
        void createIssue_Success() throws Exception {
                // Given
                IssueReqDTO issueReqDTO = new IssueReqDTO();
                issueReqDTO.setName("새 이슈");
                issueReqDTO.setContent("새 이슈 설명");

                IssueDetailDTO issueDetail = new IssueDetailDTO();

                given(issueService.createIssue(PROJECT_ID, issueReqDTO))
                                .willReturn(ApiResponse.success(issueDetail, "이슈가 생성되었습니다."));

                // When - Direct controller call to bypass aspects
                ResponseEntity<ApiResponse<IssueDetailDTO>> response = projectController.createIssue(ORG_ID, PROJECT_ID,
                                issueReqDTO);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getBody()).isNotNull();
                then(issueService).should().createIssue(PROJECT_ID, issueReqDTO);
        }

        @Test
        @DisplayName("이슈 수정 - 성공")
        void updateIssue_Success() throws Exception {
                // Given
                IssueReqDTO issueReqDTO = new IssueReqDTO();
                issueReqDTO.setName("수정된 이슈");
                issueReqDTO.setContent("수정된 이슈 설명");

                IssueDetailDTO issueDetail = new IssueDetailDTO();

                given(issueService.updateIssue(ORG_ID, ISSUE_ID, issueReqDTO))
                                .willReturn(ApiResponse.success(issueDetail, "이슈가 수정되었습니다."));

                // When - Direct controller call to bypass aspects
                ResponseEntity<ApiResponse<IssueDetailDTO>> response = projectController.updateIssue(ORG_ID, PROJECT_ID,
                                ISSUE_ID, issueReqDTO);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getBody()).isNotNull();
                then(issueService).should().updateIssue(ORG_ID, ISSUE_ID, issueReqDTO);
        }

        @Test
        @DisplayName("이슈 상태 수정 - 성공")
        void updateIssueStatus_Success() throws Exception {
                // Given
                IssueStatusReqDTO statusUpdateReqDTO = new IssueStatusReqDTO();

                IssueDetailDTO issueDetail = new IssueDetailDTO();

                given(issueService.updateIssueStatus(ISSUE_ID, statusUpdateReqDTO))
                                .willReturn(ApiResponse.success(issueDetail, "이슈 상태가 수정되었습니다."));

                // When - Direct controller call to bypass aspects
                ResponseEntity<ApiResponse<IssueDetailDTO>> response = projectController.updateIssueStatus(ORG_ID, PROJECT_ID,
                                ISSUE_ID, statusUpdateReqDTO);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getBody()).isNotNull();
                then(issueService).should().updateIssueStatus(ISSUE_ID, statusUpdateReqDTO);
        }

        @Test
        @DisplayName("이슈 삭제 - 성공")
        void deleteIssue_Success() throws Exception {
                // Given
                given(issueService.deleteIssue(ORG_ID, ISSUE_ID))
                                .willReturn(ApiResponse.success(null, "이슈가 삭제되었습니다."));

                // When & Then
                mockMvc.perform(delete("/api/organizations/{orgId}/projects/{projectId}/issues/{issueId}", ORG_ID, PROJECT_ID, ISSUE_ID))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("OK"))
                                .andExpect(jsonPath("$.message").value("이슈가 삭제되었습니다."));

                then(issueService).should().deleteIssue(ORG_ID, ISSUE_ID);
        }

        @Test
        @DisplayName("이슈 태그 목록 조회 - 성공")
        void getIssueTags_Success() throws Exception {
                // Given
                IssueTagDTO issueTag = new IssueTagDTO();
                List<IssueTagDTO> issueTags = List.of(issueTag);

                given(issueService.getIssueTags(PROJECT_ID))
                                .willReturn(ApiResponse.success(issueTags, "이슈 태그 목록을 조회했습니다."));

                // When & Then
                mockMvc.perform(get("/api/organizations/{orgId}/projects/{projectId}/issues/tags", ORG_ID, PROJECT_ID))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("OK"))
                                .andExpect(jsonPath("$.message").value("이슈 태그 목록을 조회했습니다."));

                then(issueService).should().getIssueTags(PROJECT_ID);
        }

        @Test
        @DisplayName("이슈 태그 생성 - 성공")
        void createIssueTag_Success() throws Exception {
                // Given
                IssueTagDTO issueTagDTO = new IssueTagDTO();

                given(issueService.createIssueTag(PROJECT_ID, issueTagDTO))
                                .willReturn(ApiResponse.success(null, "이슈 태그가 생성되었습니다."));

                // When - Direct controller call to bypass aspects
                ResponseEntity<ApiResponse<Void>> response = projectController.createIssueTag(PROJECT_ID, ORG_ID, issueTagDTO);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getBody()).isNotNull();
                then(issueService).should().createIssueTag(PROJECT_ID, issueTagDTO);
        }

        @Test
        @DisplayName("이슈 태그 수정 - 성공")
        void updateIssueTag_Success() throws Exception {
                // Given
                IssueTagDTO issueTagDTO = new IssueTagDTO();

                given(issueService.updateIssueTag(PROJECT_ID, ISSUE_TAG_ID, issueTagDTO))
                                .willReturn(ApiResponse.success(null, "이슈 태그가 수정되었습니다."));

                // When - Direct controller call to bypass aspects
                ResponseEntity<ApiResponse<Void>> response = projectController.updateIssueTag(ISSUE_TAG_ID, PROJECT_ID,
                                issueTagDTO);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getBody()).isNotNull();
                then(issueService).should().updateIssueTag(PROJECT_ID, ISSUE_TAG_ID, issueTagDTO);
        }

        @Test
        @DisplayName("이슈 태그 삭제 - 성공")
        void deleteIssueTag_Success() throws Exception {
                // Given
                given(issueService.deleteIssueTag(PROJECT_ID, ISSUE_TAG_ID))
                                .willReturn(ApiResponse.success(null, "이슈 태그가 삭제되었습니다."));

                // When & Then
                mockMvc.perform(delete("/api/organizations/{orgId}/projects/{projectId}/issues/tags/{issueTagId}", ORG_ID, PROJECT_ID, ISSUE_TAG_ID))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("OK"))
                                .andExpect(jsonPath("$.message").value("이슈 태그가 삭제되었습니다."));

                then(issueService).should().deleteIssueTag(PROJECT_ID, ISSUE_TAG_ID);
                SecurityContextHolder.clearContext();
        }
}