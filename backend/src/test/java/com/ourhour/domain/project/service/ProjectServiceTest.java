package com.ourhour.domain.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.exception.OrgException;
import com.ourhour.domain.org.repository.OrgRepository;
import com.ourhour.domain.project.dto.MileStoneInfoDTO;
import com.ourhour.domain.project.dto.ProjectUpdateReqDTO;
import com.ourhour.domain.project.dto.ProjectInfoDTO;
import com.ourhour.domain.project.dto.ProjectReqDTO;
import com.ourhour.domain.project.dto.ProjectSummaryResDTO;
import com.ourhour.domain.project.entity.MilestoneEntity;
import com.ourhour.domain.project.entity.ProjectEntity;
import com.ourhour.domain.project.entity.ProjectParticipantEntity;
import com.ourhour.domain.project.enums.IssueStatus;
import com.ourhour.domain.project.exception.ProjectException;
import com.ourhour.domain.project.mapper.ProjectMapper;
import com.ourhour.domain.project.repository.IssueRepository;
import com.ourhour.domain.project.repository.MilestoneRepository;
import com.ourhour.domain.project.repository.ProjectParticipantRepository;
import com.ourhour.domain.project.repository.ProjectRepository;
import com.ourhour.domain.project.validator.ProjectValidator;
import com.ourhour.domain.project.service.AuthorizationService;
import com.ourhour.domain.project.service.ProjectParticipantService;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.common.dto.PageResponse;
import com.ourhour.global.util.SecurityUtil;
import com.ourhour.domain.member.exception.MemberException;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService 테스트")
class ProjectServiceTest {

        @Mock
        private ProjectRepository projectRepository;

        @Mock
        private ProjectParticipantRepository projectParticipantRepository;

        @Mock
        private OrgRepository orgRepository;

        @Mock
        private ProjectMapper projectMapper;

        @Mock
        private MemberRepository memberRepository;

        @Mock
        private MilestoneRepository milestoneRepository;

        @Mock
        private IssueRepository issueRepository;

        @Mock
        private ProjectValidator projectValidator;

        @Mock
        private AuthorizationService authorizationService;

        @Mock
        private ProjectParticipantService projectParticipantService;

        @InjectMocks
        private ProjectService projectService;

        private OrgEntity org;
        private ProjectEntity project;
        private ProjectReqDTO projectReqDTO;
        private ProjectUpdateReqDTO projectUpdateReqDTO;
        private MilestoneEntity milestone;

        @BeforeEach
        void setUp() {
                org = mock(OrgEntity.class);
                project = mock(ProjectEntity.class);
                milestone = mock(MilestoneEntity.class);

                projectReqDTO = new ProjectReqDTO();
                projectReqDTO.setName("테스트 프로젝트");
                projectReqDTO.setDescription("테스트 프로젝트 설명");

                projectUpdateReqDTO = new ProjectUpdateReqDTO();
                projectUpdateReqDTO.setName("수정된 프로젝트");
                projectUpdateReqDTO.setDescription("수정된 프로젝트 설명");

                // 기본 Mock 동작 설정 - 필요한 경우에만 각 테스트에서 개별 설정
        }

        @Test
        @DisplayName("프로젝트 요약 목록 조회 성공")
        void getProjectsSummaryList_Success() {
                // given
                Long orgId = 1L;
                int participantLimit = 3;
                Pageable pageable = PageRequest.of(0, 10);

                given(orgRepository.existsById(orgId)).willReturn(true);
                given(project.getProjectId()).willReturn(1L);

                Page<ProjectEntity> projectPage = new PageImpl<>(List.of(project), pageable, 1);
                given(projectRepository.findByOrgEntity_OrgId(orgId, pageable)).willReturn(projectPage);

                ProjectSummaryResDTO projectSummary = new ProjectSummaryResDTO();
                given(projectMapper.toProjectSummaryResDTO(project)).willReturn(projectSummary);

                ProjectParticipantEntity participantEntity = mock(ProjectParticipantEntity.class);
                com.ourhour.domain.member.entity.MemberEntity memberEntity = mock(
                                com.ourhour.domain.member.entity.MemberEntity.class);
                given(participantEntity.getMemberEntity()).willReturn(memberEntity);
                given(participantEntity.getProjectEntity()).willReturn(project);
                given(memberEntity.getMemberId()).willReturn(1L);
                given(memberEntity.getName()).willReturn("테스트 사용자");

                List<ProjectParticipantEntity> participants = List.of(participantEntity);
                given(projectParticipantRepository.findLimitedParticipantsByProjectIds(List.of(1L), participantLimit))
                                .willReturn(participants);

                // when
                ApiResponse<PageResponse<ProjectSummaryResDTO>> result = projectService
                                .getProjectsSummaryList(orgId, participantLimit, false, pageable);

                // then
                assertThat(result).isNotNull();
                assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
                then(orgRepository).should().existsById(orgId);
                then(projectRepository).should().findByOrgEntity_OrgId(orgId, pageable);
                then(projectMapper).should().toProjectSummaryResDTO(project);
        }

        @Test
        @DisplayName("프로젝트 요약 목록 조회 시 잘못된 조직 ID로 예외 발생")
        void getProjectsSummaryList_InvalidOrgId_ThrowsException() {
                // given
                Long invalidOrgId = 0L;
                int participantLimit = 3;
                Pageable pageable = PageRequest.of(0, 10);

                // when & then
                assertThatThrownBy(() -> projectService.getProjectsSummaryList(invalidOrgId, participantLimit, false,
                                pageable))
                                .isInstanceOf(OrgException.class);
        }

        @Test
        @DisplayName("프로젝트 요약 목록 조회 시 잘못된 참여자 제한으로 예외 발생")
        void getProjectsSummaryList_InvalidParticipantLimit_ThrowsException() {
                // given
                Long orgId = 1L;
                int invalidParticipantLimit = 0;
                Pageable pageable = PageRequest.of(0, 10);

                // when & then
                assertThatThrownBy(
                                () -> projectService.getProjectsSummaryList(orgId, invalidParticipantLimit, false,
                                                pageable))
                                .isInstanceOf(ProjectException.class);
        }

        @Test
        @DisplayName("프로젝트 요약 목록 조회 시 존재하지 않는 조직으로 예외 발생")
        void getProjectsSummaryList_OrgNotFound_ThrowsException() {
                // given
                Long orgId = 999L;
                int participantLimit = 3;
                Pageable pageable = PageRequest.of(0, 10);

                given(orgRepository.existsById(orgId)).willReturn(false);

                // when & then
                assertThatThrownBy(() -> projectService.getProjectsSummaryList(orgId, participantLimit, false,
                                pageable))
                                .isInstanceOf(OrgException.class);
        }

        @Test
        @DisplayName("프로젝트 정보 조회 성공")
        void getProjectInfo_Success() {
                // given
                Long projectId = 1L;

                given(projectRepository.findById(projectId)).willReturn(Optional.of(project));

                ProjectInfoDTO projectInfo = mock(ProjectInfoDTO.class);
                given(projectMapper.toProjectInfoDTO(project)).willReturn(projectInfo);

                // when
                ApiResponse<ProjectInfoDTO> result = projectService.getProjectInfo(projectId);

                // then
                assertThat(result).isNotNull();
                assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
                then(projectRepository).should().findById(projectId);
                then(projectMapper).should().toProjectInfoDTO(project);
        }

        @Test
        @DisplayName("프로젝트 정보 조회 시 잘못된 프로젝트 ID로 예외 발생")
        void getProjectInfo_InvalidProjectId_ThrowsException() {
                // given
                Long invalidProjectId = 0L;

                // when & then
                assertThatThrownBy(() -> projectService.getProjectInfo(invalidProjectId))
                                .isInstanceOf(ProjectException.class);
        }

        @Test
        @DisplayName("프로젝트 정보 조회 시 존재하지 않는 프로젝트로 예외 발생")
        void getProjectInfo_ProjectNotFound_ThrowsException() {
                // given
                Long projectId = 999L;

                given(projectRepository.findById(projectId)).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> projectService.getProjectInfo(projectId))
                                .isInstanceOf(ProjectException.class);
        }

        @Test
        @DisplayName("프로젝트 생성 성공")
        void createProject_Success() {
                // given
                Long orgId = 1L;
                Long memberId = 1L;

                given(orgRepository.findById(orgId)).willReturn(Optional.of(org));
                given(projectMapper.toProjectEntity(org, projectReqDTO)).willReturn(project);
                given(project.getProjectId()).willReturn(1L);
                given(projectRepository.save(project)).willReturn(project);
                given(memberRepository.existsById(memberId)).willReturn(true);
                given(memberRepository.getReferenceById(memberId))
                                .willReturn(mock(com.ourhour.domain.member.entity.MemberEntity.class));

                try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
                        mockedSecurityUtil.when(() -> SecurityUtil.getCurrentMemberIdByOrgId(orgId))
                                        .thenReturn(memberId);

                        // when
                        ApiResponse<Void> result = projectService.createProject(orgId, projectReqDTO);

                        // then
                        assertThat(result).isNotNull();
                        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
                        then(orgRepository).should().findById(orgId);
                        then(projectMapper).should().toProjectEntity(org, projectReqDTO);
                        then(projectRepository).should().save(project);
                        then(projectParticipantRepository).should().save(any(ProjectParticipantEntity.class));
                }
        }

        @Test
        @DisplayName("프로젝트 생성 시 잘못된 조직 ID로 예외 발생")
        void createProject_InvalidOrgId_ThrowsException() {
                // given
                Long invalidOrgId = 0L;

                // when & then
                assertThatThrownBy(() -> projectService.createProject(invalidOrgId, projectReqDTO))
                                .isInstanceOf(OrgException.class);
        }

        @Test
        @DisplayName("프로젝트 생성 시 존재하지 않는 조직으로 예외 발생")
        void createProject_OrgNotFound_ThrowsException() {
                // given
                Long orgId = 999L;

                given(orgRepository.findById(orgId)).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> projectService.createProject(orgId, projectReqDTO))
                                .isInstanceOf(OrgException.class);
        }

        @Test
        @DisplayName("프로젝트 수정 성공")
        void updateProject_Success() {
                // given
                Long projectId = 1L;
                List<Long> participantIds = List.of(1L, 2L);
                projectUpdateReqDTO.setParticipantIds(participantIds);

                // 이 테스트에 필요한 Mock 설정
                doNothing().when(projectParticipantService).updateProjectParticipants(any(), any(), any());

                given(projectRepository.findById(projectId)).willReturn(Optional.of(project));
                given(projectRepository.save(project)).willReturn(project);

                // when
                ApiResponse<Void> result = projectService.updateProject(projectId, projectUpdateReqDTO);

                // then
                assertThat(result).isNotNull();
                assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
                then(projectRepository).should().findById(projectId);
                then(projectMapper).should().updateProjectEntity(project, projectUpdateReqDTO);
                then(projectRepository).should().save(project);
                then(projectParticipantService).should().updateProjectParticipants(any(), any(), any());
        }

        @Test
        @DisplayName("프로젝트 수정 시 잘못된 프로젝트 ID로 예외 발생")
        void updateProject_InvalidProjectId_ThrowsException() {
                // given
                Long invalidProjectId = 0L;

                // when & then
                assertThatThrownBy(() -> projectService.updateProject(invalidProjectId, projectUpdateReqDTO))
                                .isInstanceOf(ProjectException.class);
        }

        @Test
        @DisplayName("프로젝트 수정 시 존재하지 않는 프로젝트로 예외 발생")
        void updateProject_ProjectNotFound_ThrowsException() {
                // given
                Long projectId = 999L;

                given(projectRepository.findById(projectId)).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> projectService.updateProject(projectId, projectUpdateReqDTO))
                                .isInstanceOf(ProjectException.class);
        }

        @Test
        @DisplayName("프로젝트 삭제 성공")
        void deleteProject_Success() {
                // given
                Long projectId = 1L;

                // when
                ApiResponse<Void> result = projectService.deleteProject(projectId);

                // then
                assertThat(result).isNotNull();
                assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
                then(projectRepository).should().deleteById(projectId);
        }

        @Test
        @DisplayName("프로젝트 마일스톤 목록 조회 성공")
        void getProjectMilestones_Success() {
                // given
                Long orgId = 1L;
                Long projectId = 1L;
                boolean myMilestonesOnly = false;
                Pageable pageable = PageRequest.of(0, 10);

                given(projectRepository.existsById(projectId)).willReturn(true);

                Page<MilestoneEntity> milestonePage = new PageImpl<>(List.of(milestone), pageable, 1);
                given(milestoneRepository.findByProjectEntity_ProjectId(projectId, pageable))
                                .willReturn(milestonePage);

                given(milestone.getMilestoneId()).willReturn(1L);
                given(milestone.getName()).willReturn("테스트 마일스톤");

                // 벌크 쿼리 Mock 설정
                given(issueRepository.countByMilestoneIds(List.of(1L))).willReturn(List.of());
                given(issueRepository.countByMilestoneIdsAndStatus(List.of(1L), IssueStatus.COMPLETED))
                                .willReturn(List.of());

                // when
                ApiResponse<PageResponse<MileStoneInfoDTO>> result = projectService
                                .getProjectMilestones(orgId, projectId, myMilestonesOnly, pageable);

                // then
                assertThat(result).isNotNull();
                assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
                then(projectRepository).should().existsById(projectId);
                then(milestoneRepository).should().findByProjectEntity_ProjectId(projectId, pageable);
        }

        @Test
        @DisplayName("프로젝트 마일스톤 목록 조회 시 내 마일스톤만 조회 성공")
        void getProjectMilestones_MyMilestonesOnly_Success() {
                // given
                Long projectId = 1L;
                Long memberId = 1L;
                Long orgId = 1L;
                boolean myMilestonesOnly = true;
                Pageable pageable = PageRequest.of(0, 10);

                given(projectRepository.existsById(projectId)).willReturn(true);

                Page<MilestoneEntity> milestonePage = new PageImpl<>(List.of(milestone), pageable, 1);
                given(milestoneRepository.findByProjectEntity_ProjectIdWithAssignedIssues(projectId, memberId,
                                pageable))
                                .willReturn(milestonePage);

                given(milestone.getMilestoneId()).willReturn(1L);
                given(milestone.getName()).willReturn("테스트 마일스톤");

                // 벌크 쿼리 Mock 설정
                given(issueRepository.countByMilestoneIds(List.of(1L))).willReturn(List.of());
                given(issueRepository.countByMilestoneIdsAndStatus(List.of(1L), IssueStatus.COMPLETED))
                                .willReturn(List.of());

                try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
                        mockedSecurityUtil.when(() -> SecurityUtil.getCurrentMemberIdByOrgId(orgId))
                                        .thenReturn(memberId);

                        // when
                        ApiResponse<PageResponse<MileStoneInfoDTO>> result = projectService
                                        .getProjectMilestones(orgId, projectId, myMilestonesOnly, pageable);

                        // then
                        assertThat(result).isNotNull();
                        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
                        then(milestoneRepository).should().findByProjectEntity_ProjectIdWithAssignedIssues(projectId,
                                        memberId,
                                        pageable);
                }
        }

        @Test
        @DisplayName("프로젝트 마일스톤 목록 조회 시 잘못된 프로젝트 ID로 예외 발생")
        void getProjectMilestones_InvalidProjectId_ThrowsException() {
                // given
                Long orgId = 1L;
                Long invalidProjectId = 0L;
                boolean myMilestonesOnly = false;
                Pageable pageable = PageRequest.of(0, 10);

                // when & then
                assertThatThrownBy(
                                () -> projectService.getProjectMilestones(orgId, invalidProjectId, myMilestonesOnly,
                                                pageable))
                                .isInstanceOf(ProjectException.class);
        }

        @Test
        @DisplayName("프로젝트 마일스톤 목록 조회 시 존재하지 않는 프로젝트로 예외 발생")
        void getProjectMilestones_ProjectNotFound_ThrowsException() {
                // given
                Long orgId = 1L;
                Long projectId = 999L;
                boolean myMilestonesOnly = false;
                Pageable pageable = PageRequest.of(0, 10);

                given(projectRepository.existsById(projectId)).willReturn(false);

                // when & then
                assertThatThrownBy(
                                () -> projectService.getProjectMilestones(orgId, projectId, myMilestonesOnly, pageable))
                                .isInstanceOf(ProjectException.class);
        }

        @Test
        @DisplayName("내 마일스톤 조회 시 권한 없는 사용자로 예외 발생")
        void getProjectMilestones_MyMilestonesOnly_NoPermission_ThrowsException() {
                // given
                Long orgId = 1L;
                Long projectId = 1L;
                boolean myMilestonesOnly = true;
                Pageable pageable = PageRequest.of(0, 10);

                given(projectRepository.existsById(projectId)).willReturn(true);

                try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
                        mockedSecurityUtil.when(() -> SecurityUtil.getCurrentMemberIdByOrgId(orgId))
                                        .thenReturn(null);

                        // when & then
                        assertThatThrownBy(() -> projectService.getProjectMilestones(orgId, projectId, myMilestonesOnly,
                                        pageable))
                                        .isInstanceOf(MemberException.class);
                }
        }
}