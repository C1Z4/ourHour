package com.ourhour.domain.project.service;

import com.ourhour.domain.org.repository.OrgRepository;
import com.ourhour.domain.project.dto.ProjectUpdateReqDTO;
import com.ourhour.domain.project.dto.MileStoneInfoDTO;
import com.ourhour.domain.project.dto.ProjectInfoDTO;
import com.ourhour.domain.project.dto.ProjectSummaryParticipantDTO;
import com.ourhour.domain.project.dto.ProjectSummaryResDTO;
import com.ourhour.domain.project.entity.MilestoneEntity;
import com.ourhour.domain.project.dto.ProjectReqDTO;
import com.ourhour.domain.project.entity.ProjectEntity;
import com.ourhour.domain.project.entity.ProjectParticipantEntity;
import com.ourhour.domain.project.mapper.ProjectMapper;
import com.ourhour.domain.project.repository.MilestoneRepository;
import com.ourhour.domain.project.repository.IssueRepository;
import com.ourhour.domain.project.repository.ProjectParticipantRepository;
import com.ourhour.domain.project.repository.ProjectRepository;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.common.dto.PageResponse;

import com.ourhour.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.exception.OrgException;
import com.ourhour.domain.member.exception.MemberException;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.project.entity.ProjectParticipantId;
import com.ourhour.domain.project.enums.IssueStatus;
import com.ourhour.domain.project.exception.ProjectException;
import com.ourhour.domain.project.constants.ProjectConstants;

import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private static final int PERCENTAGE_MULTIPLIER = 100;

    private final ProjectRepository projectRepository;
    private final ProjectParticipantRepository projectParticipantRepository;
    private final OrgRepository orgRepository;
    private final ProjectMapper projectMapper;
    private final MemberRepository memberRepository;
    private final ProjectParticipantService projectParticipantService;

    private final MilestoneRepository milestoneRepository;
    private final IssueRepository issueRepository;

    // 프로젝트 요약 목록 조회 - 페이징 처리
    public ApiResponse<PageResponse<ProjectSummaryResDTO>> getProjectsSummaryList(Long orgId, int participantLimit,
            boolean myProjectsOnly, Pageable pageable) {

        validateProjectSummaryListParams(orgId, participantLimit);
        validateOrgExists(orgId);

        Page<ProjectEntity> projectPage = getProjectPage(orgId, myProjectsOnly, pageable);
        if (projectPage.isEmpty()) {
            return ApiResponse.success(PageResponse.empty(pageable.getPageNumber() + 1, pageable.getPageSize()));
        }

        Map<Long, List<ProjectParticipantEntity>> participantsByProject = getParticipantsByProject(projectPage,
                participantLimit);

        Page<ProjectSummaryResDTO> projectSummaryPage = convertToProjectSummaryPage(projectPage, participantsByProject);

        return ApiResponse.success(PageResponse.of(projectSummaryPage), ProjectConstants.PROJECT_SUMMARY_LIST_SUCCESS);
    }

    private void validateProjectSummaryListParams(Long orgId, int participantLimit) {
        if (orgId == null || orgId <= 0) {
            throw OrgException.orgNotFoundException();
        }
        if (participantLimit <= 0) {
            throw ProjectException.projectParticipantLimitInvalidException();
        }
    }

    private void validateOrgExists(Long orgId) {
        if (!orgRepository.existsById(orgId)) {
            throw OrgException.orgNotFoundException();
        }
    }

    private Map<Long, List<ProjectParticipantEntity>> getParticipantsByProject(
            Page<ProjectEntity> projectPage, int participantLimit) {
        List<Long> projectIds = projectPage.getContent().stream()
                .map(ProjectEntity::getProjectId)
                .toList();

        List<ProjectParticipantEntity> allParticipants = projectParticipantRepository
                .findLimitedParticipantsByProjectIds(projectIds, participantLimit);

        return allParticipants.stream()
                .collect(Collectors.groupingBy(p -> p.getProjectEntity().getProjectId()))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .limit(participantLimit)
                                .toList()));
    }

    private Page<ProjectSummaryResDTO> convertToProjectSummaryPage(
            Page<ProjectEntity> projectPage, Map<Long, List<ProjectParticipantEntity>> participantsByProject) {
        return projectPage.map(project -> {
            ProjectSummaryResDTO projectSummary = projectMapper.toProjectSummaryResDTO(project);

            List<ProjectParticipantEntity> projectParticipants = participantsByProject
                    .getOrDefault(project.getProjectId(), List.of());

            List<ProjectSummaryParticipantDTO> participants = projectParticipants.stream()
                    .map(participant -> new ProjectSummaryParticipantDTO(
                            participant.getMemberEntity().getMemberId(),
                            participant.getMemberEntity().getName()))
                    .toList();

            projectSummary.setParticipants(participants);
            return projectSummary;
        });
    }

    private Page<ProjectEntity> getProjectPage(Long orgId, boolean myProjectsOnly, Pageable pageable) {
        if (myProjectsOnly) {

            Long memberId = SecurityUtil.getCurrentMemberIdByOrgId(orgId);

            return projectRepository.findByOrgEntity_OrgIdAndParticipantMemberId(orgId, memberId, pageable);
        } else {
            return projectRepository.findByOrgEntity_OrgId(orgId, pageable);
        }
    }

    // 프로젝트 정보 조회
    public ApiResponse<ProjectInfoDTO> getProjectInfo(Long projectId) {
        if (projectId <= 0) {
            throw ProjectException.projectNotFoundException();
        }

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> ProjectException.projectNotFoundException());

        ProjectInfoDTO projectInfo = projectMapper.toProjectInfoDTO(project);

        return ApiResponse.success(projectInfo, ProjectConstants.PROJECT_INFO_SUCCESS);
    }

    // 프로젝트 등록
    @Transactional
    public ApiResponse<Void> createProject(Long orgId, ProjectReqDTO projectReqDTO) {
        if (orgId <= 0) {
            throw OrgException.orgNotFoundException();
        }

        OrgEntity orgEntity = orgRepository.findById(orgId)
                .orElseThrow(OrgException::orgNotFoundException);

        ProjectEntity projectEntity = projectMapper.toProjectEntity(orgEntity, projectReqDTO);

        projectRepository.save(projectEntity);

        // 본인도 프로젝트 참여자로 등록
        Long memberId = SecurityUtil.getCurrentMemberIdByOrgId(orgId);
        if (memberId == null) {
            throw MemberException.memberAccessDeniedException();
        }

        if (!memberRepository.existsById(memberId)) {
            throw MemberException.memberNotFoundException();
        }

        ProjectParticipantId participantId = new ProjectParticipantId(projectEntity.getProjectId(), memberId);

        ProjectParticipantEntity participant = ProjectParticipantEntity.builder()
                .projectParticipantId(participantId)
                .projectEntity(projectEntity)
                .memberEntity(memberRepository.getReferenceById(memberId))
                .build();

        projectParticipantRepository.save(participant);

        return ApiResponse.success(null, ProjectConstants.PROJECT_CREATE_SUCCESS);
    }

    // 프로젝트 수정(정보, 참가자)
    @Transactional
    public ApiResponse<Void> updateProject(Long projectId, ProjectUpdateReqDTO projectUpdateReqDTO) {
        if (projectId <= 0) {
            throw ProjectException.projectNotFoundException();
        }

        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> ProjectException.projectNotFoundException());

        projectMapper.updateProjectEntity(projectEntity, projectUpdateReqDTO);
        ProjectEntity savedProject = projectRepository.save(projectEntity);

        projectParticipantService.updateProjectParticipants(projectId, projectUpdateReqDTO.getParticipantIds(),
                savedProject);

        return ApiResponse.success(null, ProjectConstants.PROJECT_UPDATE_SUCCESS);
    }

    // 프로젝트 삭제
    @Transactional
    public ApiResponse<Void> deleteProject(Long projectId) {
        projectRepository.deleteById(projectId);
        return ApiResponse.success(null, ProjectConstants.PROJECT_DELETE_SUCCESS);
    }

    // 특정 프로젝트의 마일스톤 목록 조회
    public ApiResponse<PageResponse<MileStoneInfoDTO>> getProjectMilestones(Long orgId, Long projectId,
            boolean myMilestonesOnly, Pageable pageable) {
        if (projectId <= 0) {
            throw ProjectException.projectNotFoundException();
        }

        // 프로젝트 존재 여부 확인
        if (!projectRepository.existsById(projectId)) {
            throw ProjectException.projectNotFoundException();
        }

        Page<MilestoneEntity> milestonePage;

        if (myMilestonesOnly) {
            Long memberId = SecurityUtil.getCurrentMemberIdByOrgId(orgId);
            if (memberId == null) {
                throw MemberException.memberAccessDeniedException();
            }

            milestonePage = milestoneRepository.findByProjectEntity_ProjectIdWithAssignedIssues(projectId, memberId,
                    pageable);
        } else {
            milestonePage = milestoneRepository.findByProjectEntity_ProjectId(projectId, pageable);
        }

        if (milestonePage.isEmpty()) {
            return ApiResponse.success(PageResponse.empty(pageable.getPageNumber() + 1, pageable.getPageSize()));
        }

        // 마일스톤 ID 목록 추출
        List<Long> milestoneIds = milestonePage.getContent().stream()
                .map(MilestoneEntity::getMilestoneId)
                .toList();

        // 벌크 쿼리로 이슈 카운트 조회
        Map<Long, Long> totalCounts = issueRepository.countByMilestoneIds(milestoneIds).stream()
                .collect(Collectors.toMap(
                        count -> count.getMilestoneId(),
                        count -> count.getTotalCount()));

        Map<Long, Long> completedCounts = issueRepository
                .countByMilestoneIdsAndStatus(milestoneIds, IssueStatus.COMPLETED).stream()
                .collect(Collectors.toMap(
                        count -> count.getMilestoneId(),
                        count -> count.getCompletedCount()));

        Page<MileStoneInfoDTO> milestoneInfoPage = milestonePage.map(milestone -> {
            long totalIssues = totalCounts.getOrDefault(milestone.getMilestoneId(), 0L);
            long completedIssues = completedCounts.getOrDefault(milestone.getMilestoneId(), 0L);
            return new MileStoneInfoDTO(
                    milestone.getMilestoneId(),
                    milestone.getName(),
                    safeLongToInt(completedIssues),
                    safeLongToInt(totalIssues),
                    calculateProgressPercentage(completedIssues, totalIssues));
        });

        PageResponse<MileStoneInfoDTO> response = PageResponse.of(milestoneInfoPage);

        String message = myMilestonesOnly
                ? ProjectConstants.PROJECT_MILESTONE_MY_SUCCESS
                : ProjectConstants.PROJECT_MILESTONE_SUCCESS;
        return ApiResponse.success(response, message);
    }

    private int safeLongToInt(long value) {
        if (value > Integer.MAX_VALUE) {
            log.warn("Long value {} exceeds Integer.MAX_VALUE, returning Integer.MAX_VALUE", value);
            return Integer.MAX_VALUE;
        }
        return (int) value;
    }

    private byte calculateProgressPercentage(long completed, long total) {
        if (total == 0) {
            return 0;
        }
        long percentage = (completed * PERCENTAGE_MULTIPLIER) / total;
        return (byte) Math.min(percentage, 100);
    }

}