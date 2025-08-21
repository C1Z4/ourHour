package com.ourhour.domain.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
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
import org.springframework.http.HttpStatus;

import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.exception.MemberException;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.project.dto.IssueDetailDTO;
import com.ourhour.domain.project.dto.IssueReqDTO;
import com.ourhour.domain.project.dto.IssueStatusReqDTO;
import com.ourhour.domain.project.dto.IssueSummaryDTO;
import com.ourhour.domain.project.dto.IssueTagDTO;
import com.ourhour.domain.project.entity.IssueEntity;
import com.ourhour.domain.project.entity.IssueTagEntity;
import com.ourhour.domain.project.entity.MilestoneEntity;
import com.ourhour.domain.project.entity.ProjectEntity;
import com.ourhour.domain.project.enums.IssueStatus;
import com.ourhour.domain.project.exception.IssueException;
import com.ourhour.domain.project.exception.MilestoneException;
import com.ourhour.domain.project.exception.ProjectException;
import com.ourhour.domain.project.mapper.IssueMapper;
import com.ourhour.domain.project.mapper.IssueTagMapper;
import com.ourhour.domain.project.repository.IssueRepository;
import com.ourhour.domain.project.repository.IssueTagRepository;
import com.ourhour.domain.project.repository.MilestoneRepository;
import com.ourhour.domain.project.repository.ProjectRepository;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.common.dto.PageResponse;
import com.ourhour.global.util.SecurityUtil;

@ExtendWith(MockitoExtension.class)
@DisplayName("IssueService 테스트")
class IssueServiceTest {

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private MilestoneRepository milestoneRepository;

    @Mock
    private IssueMapper issueMapper;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProjectParticipantService projectParticipantService;

    @Mock
    private IssueTagRepository issueTagRepository;

    @Mock
    private IssueTagMapper issueTagMapper;

    @InjectMocks
    private IssueService issueService;

    private IssueEntity issue;
    private ProjectEntity project;
    private OrgEntity org;
    private MilestoneEntity milestone;
    private MemberEntity member;
    private IssueTagEntity issueTag;
    private IssueReqDTO issueReqDTO;
    private IssueStatusReqDTO issueStatusReqDTO;

    @BeforeEach
    void setUp() {
        issue = mock(IssueEntity.class);
        project = mock(ProjectEntity.class);
        org = mock(OrgEntity.class);
        milestone = mock(MilestoneEntity.class);
        member = mock(MemberEntity.class);
        issueTag = mock(IssueTagEntity.class);
        issueReqDTO = new IssueReqDTO();
        issueStatusReqDTO = new IssueStatusReqDTO();
    }

    @Test
    @DisplayName("마일스톤 이슈 목록 조회 성공 - 전체 이슈")
    void getMilestoneIssues_Success_AllIssues() {
        // given
        Long projectId = 1L;
        Long milestoneId = 1L;
        boolean myIssuesOnly = false;
        Pageable pageable = PageRequest.of(0, 10);

        given(projectRepository.existsById(projectId)).willReturn(true);
        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));
        given(project.getOrgEntity()).willReturn(org);
        given(org.getOrgId()).willReturn(1L);
        given(milestoneRepository.existsById(milestoneId)).willReturn(true);

        Page<IssueEntity> issuePage = new PageImpl<>(List.of(issue), pageable, 1);
        given(issueRepository.findByMilestoneEntity_MilestoneId(milestoneId, pageable))
                .willReturn(issuePage);

        IssueSummaryDTO issueSummary = mock(IssueSummaryDTO.class);
        given(issueMapper.toIssueSummaryDTO(issue)).willReturn(issueSummary);

        // when
        ApiResponse<PageResponse<IssueSummaryDTO>> result = issueService
                .getMilestoneIssues(projectId, milestoneId, myIssuesOnly, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        then(projectRepository).should().existsById(projectId);
        then(projectRepository).should().findById(projectId);
        then(milestoneRepository).should().existsById(milestoneId);
        then(issueRepository).should().findByMilestoneEntity_MilestoneId(milestoneId, pageable);
    }

    @Test
    @DisplayName("마일스톤 이슈 목록 조회 성공 - 내 이슈만")
    void getMilestoneIssues_Success_MyIssuesOnly() {
        // given
        Long projectId = 1L;
        Long milestoneId = 1L;
        Long memberId = 1L;
        boolean myIssuesOnly = true;
        Pageable pageable = PageRequest.of(0, 10);

        given(projectRepository.existsById(projectId)).willReturn(true);
        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));
        given(project.getOrgEntity()).willReturn(org);
        given(org.getOrgId()).willReturn(1L);
        given(milestoneRepository.existsById(milestoneId)).willReturn(true);

        Page<IssueEntity> issuePage = new PageImpl<>(List.of(issue), pageable, 1);
        given(issueRepository.findByMilestoneEntity_MilestoneIdAndAssigneeEntity_MemberId(milestoneId, memberId,
                pageable))
                .willReturn(issuePage);

        IssueSummaryDTO issueSummary = mock(IssueSummaryDTO.class);
        given(issueMapper.toIssueSummaryDTO(issue)).willReturn(issueSummary);

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(() -> SecurityUtil.getCurrentMemberIdByOrgId(1L))
                    .thenReturn(memberId);

            // when
            ApiResponse<PageResponse<IssueSummaryDTO>> result = issueService
                    .getMilestoneIssues(projectId, milestoneId, myIssuesOnly, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        }
    }

    @Test
    @DisplayName("마일스톤 이슈 목록 조회 시 잘못된 프로젝트 ID로 예외 발생")
    void getMilestoneIssues_InvalidProjectId_ThrowsException() {
        // given
        Long invalidProjectId = 0L;
        Long milestoneId = 1L;
        boolean myIssuesOnly = false;
        Pageable pageable = PageRequest.of(0, 10);

        // when & then
        assertThatThrownBy(() -> issueService.getMilestoneIssues(invalidProjectId, milestoneId, myIssuesOnly, pageable))
                .isInstanceOf(ProjectException.class);
    }

    @Test
    @DisplayName("마일스톤 이슈 목록 조회 시 존재하지 않는 프로젝트로 예외 발생")
    void getMilestoneIssues_ProjectNotFound_ThrowsException() {
        // given
        Long projectId = 999L;
        Long milestoneId = 1L;
        boolean myIssuesOnly = false;
        Pageable pageable = PageRequest.of(0, 10);

        given(projectRepository.existsById(projectId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> issueService.getMilestoneIssues(projectId, milestoneId, myIssuesOnly, pageable))
                .isInstanceOf(ProjectException.class);
    }

    @Test
    @DisplayName("마일스톤 이슈 목록 조회 시 존재하지 않는 마일스톤으로 예외 발생")
    void getMilestoneIssues_MilestoneNotFound_ThrowsException() {
        // given
        Long projectId = 1L;
        Long milestoneId = 999L;
        boolean myIssuesOnly = false;
        Pageable pageable = PageRequest.of(0, 10);

        given(projectRepository.existsById(projectId)).willReturn(true);
        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));
        given(project.getOrgEntity()).willReturn(org);
        given(org.getOrgId()).willReturn(1L);
        given(milestoneRepository.existsById(milestoneId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> issueService.getMilestoneIssues(projectId, milestoneId, myIssuesOnly, pageable))
                .isInstanceOf(MilestoneException.class);
    }

    @Test
    @DisplayName("이슈 상세 조회 성공")
    void getIssueDetail_Success() {
        // given
        Long issueId = 1L;

        given(issueRepository.findById(issueId)).willReturn(Optional.of(issue));

        IssueDetailDTO issueDetail = mock(IssueDetailDTO.class);
        given(issueMapper.toIssueDetailDTO(issue)).willReturn(issueDetail);

        // when
        ApiResponse<IssueDetailDTO> result = issueService.getIssueDetail(issueId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        then(issueRepository).should().findById(issueId);
        then(issueMapper).should().toIssueDetailDTO(issue);
    }

    @Test
    @DisplayName("이슈 상세 조회 시 잘못된 이슈 ID로 예외 발생")
    void getIssueDetail_InvalidIssueId_ThrowsException() {
        // given
        Long invalidIssueId = 0L;

        // when & then
        assertThatThrownBy(() -> issueService.getIssueDetail(invalidIssueId))
                .isInstanceOf(IssueException.class);
    }

    @Test
    @DisplayName("이슈 상세 조회 시 존재하지 않는 이슈로 예외 발생")
    void getIssueDetail_IssueNotFound_ThrowsException() {
        // given
        Long issueId = 999L;

        given(issueRepository.findById(issueId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> issueService.getIssueDetail(issueId))
                .isInstanceOf(IssueException.class);
    }

    @Test
    @DisplayName("이슈 등록 성공")
    void createIssue_Success() {
        // given
        Long projectId = 1L;
        Long milestoneId = 1L;
        Long assigneeId = 1L;

        issueReqDTO.setMilestoneId(milestoneId);
        issueReqDTO.setAssigneeId(assigneeId);

        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));
        given(milestoneRepository.findById(milestoneId)).willReturn(Optional.of(milestone));
        given(memberRepository.findById(assigneeId)).willReturn(Optional.of(member));
        given(issueMapper.toIssueEntity(issueReqDTO)).willReturn(issue);
        given(issueRepository.save(issue)).willReturn(issue);

        IssueDetailDTO issueDetail = mock(IssueDetailDTO.class);
        given(issueMapper.toIssueDetailDTO(issue)).willReturn(issueDetail);

        // when
        ApiResponse<IssueDetailDTO> result = issueService.createIssue(projectId, issueReqDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        then(projectRepository).should().findById(projectId);
        then(milestoneRepository).should().findById(milestoneId);
        then(memberRepository).should().findById(assigneeId);
        then(issueMapper).should().toIssueEntity(issueReqDTO);
        then(issue).should().setProjectEntity(project);
        then(issue).should().setMilestoneEntity(milestone);
        then(issue).should().setAssigneeEntity(member);
        then(issueRepository).should().save(issue);
    }

    @Test
    @DisplayName("이슈 등록 시 잘못된 프로젝트 ID로 예외 발생")
    void createIssue_InvalidProjectId_ThrowsException() {
        // given
        Long invalidProjectId = 0L;

        // when & then
        assertThatThrownBy(() -> issueService.createIssue(invalidProjectId, issueReqDTO))
                .isInstanceOf(ProjectException.class);
    }

    @Test
    @DisplayName("이슈 등록 시 존재하지 않는 프로젝트로 예외 발생")
    void createIssue_ProjectNotFound_ThrowsException() {
        // given
        Long projectId = 999L;

        given(projectRepository.findById(projectId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> issueService.createIssue(projectId, issueReqDTO))
                .isInstanceOf(ProjectException.class);
    }

    @Test
    @DisplayName("이슈 등록 시 존재하지 않는 마일스톤으로 예외 발생")
    void createIssue_MilestoneNotFound_ThrowsException() {
        // given
        Long projectId = 1L;
        Long milestoneId = 999L;

        issueReqDTO.setMilestoneId(milestoneId);

        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));
        given(issueMapper.toIssueEntity(issueReqDTO)).willReturn(issue);
        given(milestoneRepository.findById(milestoneId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> issueService.createIssue(projectId, issueReqDTO))
                .isInstanceOf(MilestoneException.class);
    }

    @Test
    @DisplayName("이슈 등록 시 존재하지 않는 담당자로 예외 발생")
    void createIssue_AssigneeNotFound_ThrowsException() {
        // given
        Long projectId = 1L;
        Long assigneeId = 999L;

        issueReqDTO.setAssigneeId(assigneeId);

        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));
        given(issueMapper.toIssueEntity(issueReqDTO)).willReturn(issue);
        given(memberRepository.findById(assigneeId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> issueService.createIssue(projectId, issueReqDTO))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("이슈 수정 성공")
    void updateIssue_Success() {
        // given
        Long issueId = 1L;
        Long orgId = 1L;
        Long projectId = 1L;
        Long memberId = 1L;
        Long assigneeId = 1L;
        Long milestoneId = 1L;
        Long issueTagId = 1L;

        issueReqDTO.setAssigneeId(assigneeId);
        issueReqDTO.setMilestoneId(milestoneId);
        issueReqDTO.setIssueTagId(issueTagId);

        given(issueRepository.findById(issueId)).willReturn(Optional.of(issue));
        given(issue.getProjectEntity()).willReturn(project);
        given(project.getOrgEntity()).willReturn(org);
        given(org.getOrgId()).willReturn(orgId);
        given(project.getProjectId()).willReturn(projectId);
        given(memberRepository.findById(assigneeId)).willReturn(Optional.of(member));
        given(milestoneRepository.findById(milestoneId)).willReturn(Optional.of(milestone));
        given(issueTagRepository.findById(issueTagId)).willReturn(Optional.of(issueTag));
        given(issueRepository.save(issue)).willReturn(issue);

        IssueDetailDTO issueDetail = mock(IssueDetailDTO.class);
        given(issueMapper.toIssueDetailDTO(issue)).willReturn(issueDetail);

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(() -> SecurityUtil.getCurrentMemberIdByOrgId(orgId))
                    .thenReturn(memberId);
            mockedSecurityUtil.when(() -> SecurityUtil.getCurrentRoleByOrgId(orgId))
                    .thenReturn(Role.MEMBER);

            given(projectParticipantService.isProjectParticipant(projectId, memberId)).willReturn(true);

            // when
            ApiResponse<IssueDetailDTO> result = issueService.updateIssue(issueId, issueReqDTO);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        }
    }

    @Test
    @DisplayName("이슈 수정 시 잘못된 이슈 ID로 예외 발생")
    void updateIssue_InvalidIssueId_ThrowsException() {
        // given
        Long invalidIssueId = 0L;

        // when & then
        assertThatThrownBy(() -> issueService.updateIssue(invalidIssueId, issueReqDTO))
                .isInstanceOf(IssueException.class);
    }

    @Test
    @DisplayName("이슈 수정 시 존재하지 않는 이슈로 예외 발생")
    void updateIssue_IssueNotFound_ThrowsException() {
        // given
        Long issueId = 999L;

        given(issueRepository.findById(issueId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> issueService.updateIssue(issueId, issueReqDTO))
                .isInstanceOf(IssueException.class);
    }

    @Test
    @DisplayName("이슈 수정 시 권한 없는 사용자로 예외 발생")
    void updateIssue_NoPermission_ThrowsException() {
        // given
        Long issueId = 1L;
        Long orgId = 1L;
        Long projectId = 1L;

        given(issueRepository.findById(issueId)).willReturn(Optional.of(issue));
        given(issue.getProjectEntity()).willReturn(project);
        given(project.getOrgEntity()).willReturn(org);
        given(org.getOrgId()).willReturn(orgId);
        given(project.getProjectId()).willReturn(projectId);

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(() -> SecurityUtil.getCurrentMemberIdByOrgId(orgId))
                    .thenReturn(null);

            // when & then
            assertThatThrownBy(() -> issueService.updateIssue(issueId, issueReqDTO))
                    .isInstanceOf(MemberException.class);
        }
    }

    @Test
    @DisplayName("이슈 상태 수정 성공")
    void updateIssueStatus_Success() {
        // given
        Long issueId = 1L;
        IssueStatus newStatus = IssueStatus.IN_PROGRESS;
        issueStatusReqDTO.setStatus(newStatus);

        given(issueRepository.findById(issueId)).willReturn(Optional.of(issue));
        given(issueRepository.save(issue)).willReturn(issue);

        IssueDetailDTO issueDetail = mock(IssueDetailDTO.class);
        given(issueMapper.toIssueDetailDTO(issue)).willReturn(issueDetail);

        // when
        ApiResponse<IssueDetailDTO> result = issueService.updateIssueStatus(issueId, issueStatusReqDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        then(issueRepository).should().findById(issueId);
        then(issueRepository).should().save(issue);
    }

    @Test
    @DisplayName("이슈 상태 수정 시 잘못된 이슈 ID로 예외 발생")
    void updateIssueStatus_InvalidIssueId_ThrowsException() {
        // given
        Long invalidIssueId = 0L;

        // when & then
        assertThatThrownBy(() -> issueService.updateIssueStatus(invalidIssueId, issueStatusReqDTO))
                .isInstanceOf(IssueException.class);
    }

    @Test
    @DisplayName("이슈 상태 수정 시 존재하지 않는 이슈로 예외 발생")
    void updateIssueStatus_IssueNotFound_ThrowsException() {
        // given
        Long issueId = 999L;

        given(issueRepository.findById(issueId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> issueService.updateIssueStatus(issueId, issueStatusReqDTO))
                .isInstanceOf(IssueException.class);
    }

    @Test
    @DisplayName("이슈 삭제 성공")
    void deleteIssue_Success() {
        // given
        Long issueId = 1L;
        Long orgId = 1L;
        Long projectId = 1L;
        Long memberId = 1L;

        given(issueRepository.findById(issueId)).willReturn(Optional.of(issue));
        given(issue.getProjectEntity()).willReturn(project);
        given(project.getOrgEntity()).willReturn(org);
        given(org.getOrgId()).willReturn(orgId);
        given(project.getProjectId()).willReturn(projectId);

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(() -> SecurityUtil.getCurrentMemberIdByOrgId(orgId))
                    .thenReturn(memberId);
            mockedSecurityUtil.when(() -> SecurityUtil.getCurrentRoleByOrgId(orgId))
                    .thenReturn(Role.MEMBER);

            given(projectParticipantService.isProjectParticipant(projectId, memberId)).willReturn(true);

            // when
            ApiResponse<Void> result = issueService.deleteIssue(issueId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
            then(issueRepository).should().findById(issueId);
            then(issueRepository).should().deleteById(issueId);
        }
    }

    @Test
    @DisplayName("이슈 삭제 시 잘못된 이슈 ID로 예외 발생")
    void deleteIssue_InvalidIssueId_ThrowsException() {
        // given
        Long invalidIssueId = 0L;

        // when & then
        assertThatThrownBy(() -> issueService.deleteIssue(invalidIssueId))
                .isInstanceOf(IssueException.class);
    }

    @Test
    @DisplayName("이슈 삭제 시 존재하지 않는 이슈로 예외 발생")
    void deleteIssue_IssueNotFound_ThrowsException() {
        // given
        Long issueId = 999L;

        given(issueRepository.findById(issueId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> issueService.deleteIssue(issueId))
                .isInstanceOf(IssueException.class);
    }

    @Test
    @DisplayName("이슈 삭제 시 권한 없는 사용자로 예외 발생")
    void deleteIssue_NoPermission_ThrowsException() {
        // given
        Long issueId = 1L;
        Long orgId = 1L;
        Long projectId = 1L;

        given(issueRepository.findById(issueId)).willReturn(Optional.of(issue));
        given(issue.getProjectEntity()).willReturn(project);
        given(project.getOrgEntity()).willReturn(org);
        given(org.getOrgId()).willReturn(orgId);
        given(project.getProjectId()).willReturn(projectId);

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(() -> SecurityUtil.getCurrentMemberIdByOrgId(orgId))
                    .thenReturn(null);

            // when & then
            assertThatThrownBy(() -> issueService.deleteIssue(issueId))
                    .isInstanceOf(MemberException.class);
        }
    }

    @Test
    @DisplayName("이슈 태그 조회 성공")
    void getIssueTags_Success() {
        // given
        Long projectId = 1L;

        List<IssueTagEntity> issueTagEntities = List.of(issueTag);
        given(issueTagRepository.findByProjectEntity_ProjectId(projectId)).willReturn(issueTagEntities);

        IssueTagDTO issueTagDTO = mock(IssueTagDTO.class);
        given(issueTagMapper.toIssueTagDTO(issueTag)).willReturn(issueTagDTO);

        // when
        ApiResponse<List<IssueTagDTO>> result = issueService.getIssueTags(projectId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        then(issueTagRepository).should().findByProjectEntity_ProjectId(projectId);
        then(issueTagMapper).should().toIssueTagDTO(issueTag);
    }

    @Test
    @DisplayName("이슈 태그 조회 시 잘못된 프로젝트 ID로 예외 발생")
    void getIssueTags_InvalidProjectId_ThrowsException() {
        // given
        Long invalidProjectId = 0L;

        // when & then
        assertThatThrownBy(() -> issueService.getIssueTags(invalidProjectId))
                .isInstanceOf(ProjectException.class);
    }

    @Test
    @DisplayName("이슈 태그 조회 시 빈 결과 반환")
    void getIssueTags_EmptyResult_Success() {
        // given
        Long projectId = 1L;

        given(issueTagRepository.findByProjectEntity_ProjectId(projectId)).willReturn(List.of());

        // when
        ApiResponse<List<IssueTagDTO>> result = issueService.getIssueTags(projectId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("이슈 태그 등록 성공")
    void createIssueTag_Success() {
        // given
        Long projectId = 1L;
        IssueTagDTO issueTagDTO = mock(IssueTagDTO.class);

        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));
        given(issueTagMapper.toIssueTagEntity(issueTagDTO)).willReturn(issueTag);
        given(issueTagRepository.save(issueTag)).willReturn(issueTag);

        // when
        ApiResponse<Void> result = issueService.createIssueTag(projectId, issueTagDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        then(projectRepository).should().findById(projectId);
        then(issueTagMapper).should().toIssueTagEntity(issueTagDTO);
        then(issueTagRepository).should().save(issueTag);
    }

    @Test
    @DisplayName("이슈 태그 등록 시 잘못된 프로젝트 ID로 예외 발생")
    void createIssueTag_InvalidProjectId_ThrowsException() {
        // given
        Long invalidProjectId = 0L;
        IssueTagDTO issueTagDTO = mock(IssueTagDTO.class);

        // when & then
        assertThatThrownBy(() -> issueService.createIssueTag(invalidProjectId, issueTagDTO))
                .isInstanceOf(ProjectException.class);
    }

    @Test
    @DisplayName("이슈 태그 등록 시 존재하지 않는 프로젝트로 예외 발생")
    void createIssueTag_ProjectNotFound_ThrowsException() {
        // given
        Long projectId = 999L;
        IssueTagDTO issueTagDTO = mock(IssueTagDTO.class);

        given(projectRepository.findById(projectId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> issueService.createIssueTag(projectId, issueTagDTO))
                .isInstanceOf(ProjectException.class);
    }

    @Test
    @DisplayName("이슈 태그 수정 성공")
    void updateIssueTag_Success() {
        // given
        Long projectId = 1L;
        Long issueTagId = 1L;
        IssueTagDTO issueTagDTO = mock(IssueTagDTO.class);

        given(issueTagRepository.findById(issueTagId)).willReturn(Optional.of(issueTag));

        // when
        ApiResponse<Void> result = issueService.updateIssueTag(projectId, issueTagId, issueTagDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        then(issueTagRepository).should().findById(issueTagId);
        then(issueTagMapper).should().updateIssueTagEntity(issueTag, issueTagDTO);
    }

    @Test
    @DisplayName("이슈 태그 수정 시 잘못된 프로젝트 ID로 예외 발생")
    void updateIssueTag_InvalidProjectId_ThrowsException() {
        // given
        Long invalidProjectId = 0L;
        Long issueTagId = 1L;
        IssueTagDTO issueTagDTO = mock(IssueTagDTO.class);

        // when & then
        assertThatThrownBy(() -> issueService.updateIssueTag(invalidProjectId, issueTagId, issueTagDTO))
                .isInstanceOf(ProjectException.class);
    }

    @Test
    @DisplayName("이슈 태그 수정 시 잘못된 이슈 태그 ID로 예외 발생")
    void updateIssueTag_InvalidIssueTagId_ThrowsException() {
        // given
        Long projectId = 1L;
        Long invalidIssueTagId = 0L;
        IssueTagDTO issueTagDTO = mock(IssueTagDTO.class);

        // when & then
        assertThatThrownBy(() -> issueService.updateIssueTag(projectId, invalidIssueTagId, issueTagDTO))
                .isInstanceOf(IssueException.class);
    }

    @Test
    @DisplayName("이슈 태그 수정 시 존재하지 않는 이슈 태그로 예외 발생")
    void updateIssueTag_IssueTagNotFound_ThrowsException() {
        // given
        Long projectId = 1L;
        Long issueTagId = 999L;
        IssueTagDTO issueTagDTO = mock(IssueTagDTO.class);

        given(issueTagRepository.findById(issueTagId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> issueService.updateIssueTag(projectId, issueTagId, issueTagDTO))
                .isInstanceOf(IssueException.class);
    }

    @Test
    @DisplayName("이슈 태그 삭제 성공")
    void deleteIssueTag_Success() {
        // given
        Long projectId = 1L;
        Long issueTagId = 1L;

        given(issueTagRepository.findById(issueTagId)).willReturn(Optional.of(issueTag));

        // when
        ApiResponse<Void> result = issueService.deleteIssueTag(projectId, issueTagId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        then(issueTagRepository).should().findById(issueTagId);
        then(issueTagRepository).should().delete(issueTag);
    }

    @Test
    @DisplayName("이슈 태그 삭제 시 잘못된 프로젝트 ID로 예외 발생")
    void deleteIssueTag_InvalidProjectId_ThrowsException() {
        // given
        Long invalidProjectId = 0L;
        Long issueTagId = 1L;

        // when & then
        assertThatThrownBy(() -> issueService.deleteIssueTag(invalidProjectId, issueTagId))
                .isInstanceOf(ProjectException.class);
    }

    @Test
    @DisplayName("이슈 태그 삭제 시 잘못된 이슈 태그 ID로 예외 발생")
    void deleteIssueTag_InvalidIssueTagId_ThrowsException() {
        // given
        Long projectId = 1L;
        Long invalidIssueTagId = 0L;

        // when & then
        assertThatThrownBy(() -> issueService.deleteIssueTag(projectId, invalidIssueTagId))
                .isInstanceOf(IssueException.class);
    }

    @Test
    @DisplayName("이슈 태그 삭제 시 존재하지 않는 이슈 태그로 예외 발생")
    void deleteIssueTag_IssueTagNotFound_ThrowsException() {
        // given
        Long projectId = 1L;
        Long issueTagId = 999L;

        given(issueTagRepository.findById(issueTagId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> issueService.deleteIssueTag(projectId, issueTagId))
                .isInstanceOf(IssueException.class);
    }
}