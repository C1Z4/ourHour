package com.ourhour.domain.project.service;

import java.util.List;
import java.util.stream.Collectors;

import com.ourhour.domain.org.exception.OrgException;
import com.ourhour.domain.project.dto.IssueTagDTO;
import com.ourhour.domain.project.dto.IssueDetailDTO;
import com.ourhour.domain.project.dto.IssueSummaryDTO;
import com.ourhour.domain.project.dto.IssueReqDTO;
import com.ourhour.domain.project.dto.IssueStatusReqDTO;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.common.dto.PageResponse;
import com.ourhour.global.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.project.entity.IssueEntity;
import com.ourhour.domain.project.entity.IssueTagEntity;
import com.ourhour.domain.project.entity.MilestoneEntity;
import com.ourhour.domain.project.entity.ProjectEntity;
import com.ourhour.domain.project.enums.IssueStatus;
import com.ourhour.domain.project.enums.SyncOperation;
import com.ourhour.domain.project.mapper.IssueMapper;
import com.ourhour.domain.project.mapper.IssueTagMapper;
import com.ourhour.domain.project.repository.IssueRepository;
import com.ourhour.domain.project.repository.IssueTagRepository;
import com.ourhour.domain.project.repository.ProjectRepository;
import com.ourhour.domain.project.repository.MilestoneRepository;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.exception.MemberException;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.project.exception.ProjectException;
import com.ourhour.domain.project.exception.MilestoneException;
import com.ourhour.domain.project.exception.IssueException;
import com.ourhour.domain.project.annotation.GitHubSync;
import com.ourhour.domain.notification.dto.IssueNotificationContext;
import com.ourhour.domain.notification.service.NotificationEventService;
import com.ourhour.domain.project.constants.ProjectConstants;
import com.ourhour.domain.project.validator.ProjectValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final MilestoneRepository milestoneRepository;
    private final IssueMapper issueMapper;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final ProjectParticipantService projectParticipantService;
    private final IssueTagRepository issueTagRepository;
    private final IssueTagMapper issueTagMapper;
    private final NotificationEventService notificationEventService;
    private final ProjectValidator projectValidator;

    // 특정 마일스톤의 이슈 목록 조회 (milestoneId가 null이면 마일스톤이 할당되지 않은 이슈들 조회)
    public ApiResponse<PageResponse<IssueSummaryDTO>> getMilestoneIssues(Long projectId, Long milestoneId,
            boolean myIssuesOnly, Pageable pageable) {
        projectValidator.validateProjectId(projectId);

        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> ProjectException.projectNotFoundException());

        Long orgId = projectEntity.getOrgEntity().getOrgId();

        if (myIssuesOnly) {
            Long memberId = SecurityUtil.getCurrentMemberIdByOrgId(orgId);
            if (memberId == null) {
                throw MemberException.memberAccessDeniedException();
            }

            Page<IssueEntity> issuePage;

            if (projectValidator.isValidMilestoneId(milestoneId)) {
                // 특정 마일스톤의 내가 할당된 이슈 조회
                issuePage = issueRepository.findByMilestoneEntity_MilestoneIdAndAssigneeEntity_MemberId(
                        milestoneId, memberId, pageable);
            } else {
                // 마일스톤이 할당되지 않은 내가 할당된 이슈 조회
                issuePage = issueRepository
                        .findByProjectEntity_ProjectIdAndMilestoneEntityIsNullAndAssigneeEntity_MemberId(
                                projectId, memberId, pageable);
            }

            if (issuePage.isEmpty()) {
                return ApiResponse.success(PageResponse.empty(pageable.getPageNumber() + 1, pageable.getPageSize()));
            }

            String message = projectValidator.isValidMilestoneId(milestoneId)
                    ? ProjectConstants.MILESTONE_ISSUES_MY_SUCCESS
                    : ProjectConstants.MILESTONE_ISSUES_UNASSIGNED_MY_SUCCESS;

            return ApiResponse.success(PageResponse.of(issuePage.map(issueMapper::toIssueSummaryDTO)), message);
        }

        Page<IssueEntity> issuePage;

        if (projectValidator.isValidMilestoneId(milestoneId)) {
            // 특정 마일스톤의 이슈 조회
            issuePage = issueRepository.findByMilestoneEntity_MilestoneId(milestoneId, pageable);
        } else {
            // 마일스톤이 할당되지 않은 이슈들 조회
            issuePage = issueRepository.findByProjectEntity_ProjectIdAndMilestoneEntityIsNull(projectId, pageable);
        }

        if (issuePage.isEmpty()) {
            return ApiResponse.success(PageResponse.empty(pageable.getPageNumber() + 1, pageable.getPageSize()));
        }

        Page<IssueSummaryDTO> issueDTOPage = issuePage.map(issueMapper::toIssueSummaryDTO);

        String message = projectValidator.isValidMilestoneId(milestoneId)
                ? ProjectConstants.MILESTONE_ISSUES_SUCCESS
                : ProjectConstants.MILESTONE_ISSUES_UNASSIGNED_SUCCESS;

        return ApiResponse.success(PageResponse.of(issueDTOPage), message);
    }

    // 이슈 상세 조회
    public ApiResponse<IssueDetailDTO> getIssueDetail(Long issueId) {
        if (issueId <= 0) {
            throw IssueException.issueNotFoundException();
        }

        IssueEntity issueEntity = issueRepository.findById(issueId)
                .orElseThrow(() -> IssueException.issueNotFoundException());

        IssueDetailDTO issueDetailDTO = issueMapper.toIssueDetailDTO(issueEntity);

        return ApiResponse.success(issueDetailDTO, ProjectConstants.ISSUE_DETAIL_SUCCESS);
    }

    // 이슈 등록
    @GitHubSync(operation = SyncOperation.CREATE)
    @Transactional
    public ApiResponse<IssueDetailDTO> createIssue(Long projectId, IssueReqDTO issueReqDTO) {
        projectValidator.validateProjectId(projectId);

        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> ProjectException.projectNotFoundException());

        IssueEntity issueEntity = issueMapper.toIssueEntity(issueReqDTO);

        issueEntity.setProjectEntity(projectEntity);

        // 마일스톤 설정
        if (projectValidator.isValidMilestoneId(issueReqDTO.getMilestoneId())) {
            MilestoneEntity milestoneEntity = milestoneRepository.findById(issueReqDTO.getMilestoneId())
                    .orElseThrow(() -> MilestoneException.milestoneNotFoundException());
            issueEntity.setMilestoneEntity(milestoneEntity);
        }

        // 담당자 설정
        if (projectValidator.isValidAssigneeId(issueReqDTO.getAssigneeId())) {
            MemberEntity assigneeEntity = memberRepository.findById(issueReqDTO.getAssigneeId())
                    .orElseThrow(() -> MemberException.memberNotFoundException());
            issueEntity.setAssigneeEntity(assigneeEntity);
        }

        IssueEntity savedIssueEntity = issueRepository.save(issueEntity);

        Long currentMemberId = SecurityUtil.getCurrentMemberIdByOrgId(projectEntity.getOrgEntity().getOrgId());
        sendIssueAssignedNotificationIfNeeded(savedIssueEntity, currentMemberId);

        IssueDetailDTO issueDetailDTO = issueMapper.toIssueDetailDTO(savedIssueEntity);

        return ApiResponse.success(issueDetailDTO, ProjectConstants.ISSUE_CREATE_SUCCESS);
    }

    // 이슈 수정
    @GitHubSync(operation = SyncOperation.UPDATE)
    @Transactional
    public ApiResponse<IssueDetailDTO> updateIssue(Long orgId, Long issueId, IssueReqDTO issueReqDTO) {
        IssueEntity issueEntity = validateAndGetIssue(issueId);
        validateProjectParticipantOrAdmin(orgId, issueEntity.getProjectEntity().getProjectId());

        MemberEntity previousAssignee = issueEntity.getAssigneeEntity();
        updateIssueEntityFromRequest(issueEntity, issueReqDTO);

        IssueEntity savedIssueEntity = issueRepository.save(issueEntity);
        sendNotificationIfAssigneeChanged(savedIssueEntity, previousAssignee, orgId);

        IssueDetailDTO issueDetailDTO = issueMapper.toIssueDetailDTO(savedIssueEntity);
        return ApiResponse.success(issueDetailDTO, ProjectConstants.ISSUE_UPDATE_SUCCESS);
    }

    // 이슈 상태 수정
    @GitHubSync(operation = SyncOperation.UPDATE)
    @Transactional
    public ApiResponse<IssueDetailDTO> updateIssueStatus(Long issueId, IssueStatusReqDTO issueStatusReqDTO) {
        if (issueId <= 0) {
            throw IssueException.issueNotFoundException();
        }

        IssueEntity issueEntity = issueRepository.findById(issueId)
                .orElseThrow(() -> IssueException.issueNotFoundException());

        issueEntity.setStatus(issueStatusReqDTO.getStatus());

        IssueEntity savedIssueEntity = issueRepository.save(issueEntity);

        IssueDetailDTO issueDetailDTO = issueMapper.toIssueDetailDTO(savedIssueEntity);

        return ApiResponse.success(issueDetailDTO, ProjectConstants.ISSUE_STATUS_UPDATE_SUCCESS);
    }

    // 이슈 삭제
    @GitHubSync(operation = SyncOperation.DELETE, entityParam = "issueId")
    @Transactional
    public ApiResponse<Void> deleteIssue(Long orgId, Long issueId) {
        if (issueId <= 0) {
            throw IssueException.issueNotFoundException();
        }

        IssueEntity issueEntity = issueRepository.findById(issueId)
                .orElseThrow(() -> IssueException.issueNotFoundException());

        validateProjectParticipantOrAdmin(orgId, issueEntity.getProjectEntity().getProjectId());

        issueRepository.deleteById(issueId);

        return ApiResponse.success(null, ProjectConstants.ISSUE_DELETE_SUCCESS);
    }

    // 이슈 태그 조회
    public ApiResponse<List<IssueTagDTO>> getIssueTags(Long projectId) {
        projectValidator.validateProjectId(projectId);

        List<IssueTagEntity> issueTagEntities = issueTagRepository.findByProjectEntity_ProjectId(projectId);

        if (issueTagEntities.isEmpty()) {
            return ApiResponse.success(null, ProjectConstants.ISSUE_TAG_NOT_FOUND);
        }

        return ApiResponse.success(issueTagEntities.stream()
                .map(issueTagMapper::toIssueTagDTO)
                .toList(), ProjectConstants.ISSUE_TAG_GET_SUCCESS);
    }

    // 이슈 태그 등록
    @Transactional
    public ApiResponse<Void> createIssueTag(Long projectId, IssueTagDTO issueTagDTO) {
        projectValidator.validateProjectId(projectId);

        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> ProjectException.projectNotFoundException());

        IssueTagEntity issueTagEntity = issueTagMapper.toIssueTagEntity(issueTagDTO);

        issueTagEntity.setProjectEntity(projectEntity);

        issueTagRepository.save(issueTagEntity);

        return ApiResponse.success(null, ProjectConstants.ISSUE_TAG_CREATE_SUCCESS);
    }

    // 이슈 태그 수정
    @Transactional
    public ApiResponse<Void> updateIssueTag(Long projectId, Long issueTagId, IssueTagDTO issueTagDTO) {
        projectValidator.validateProjectId(projectId);

        projectValidator.validateIssueTagId(issueTagId);

        IssueTagEntity issueTagEntity = issueTagRepository.findById(issueTagId)
                .orElseThrow(() -> IssueException.issueTagNotFoundException());

        issueTagMapper.updateIssueTagEntity(issueTagEntity, issueTagDTO);

        return ApiResponse.success(null, ProjectConstants.ISSUE_TAG_UPDATE_SUCCESS);
    }

    // 이슈 태그 삭제
    @Transactional
    public ApiResponse<Void> deleteIssueTag(Long projectId, Long issueTagId) {
        projectValidator.validateProjectId(projectId);

        projectValidator.validateIssueTagId(issueTagId);

        IssueTagEntity issueTagEntity = issueTagRepository.findById(issueTagId)
                .orElseThrow(() -> IssueException.issueTagNotFoundException());

        issueTagRepository.delete(issueTagEntity);

        return ApiResponse.success(null, ProjectConstants.ISSUE_TAG_DELETE_SUCCESS);
    }

    private void sendIssueAssignedNotificationIfNeeded(IssueEntity issueEntity, Long currentMemberId) {
        if (issueEntity.getAssigneeEntity() == null) {
            return;
        }

        Long assigneeId = issueEntity.getAssigneeEntity().getMemberId();
        if (currentMemberId != null && currentMemberId.equals(assigneeId)) {
            return;
        }

        Long targetUserId = issueEntity.getAssigneeEntity().getUserEntity().getUserId();
        ProjectEntity projectEntity = issueEntity.getProjectEntity();

        notificationEventService.sendIssueAssignedNotification(
                IssueNotificationContext.builder()
                        .userId(targetUserId)
                        .issueTitle(issueEntity.getName())
                        .issueId(issueEntity.getIssueId())
                        .projectId(projectEntity.getProjectId())
                        .orgId(projectEntity.getOrgEntity().getOrgId())
                        .projectName(projectEntity.getName())
                        .build());
    }

    private IssueEntity validateAndGetIssue(Long issueId) {
        projectValidator.validateIssueId(issueId);
        return issueRepository.findById(issueId)
                .orElseThrow(() -> IssueException.issueNotFoundException());
    }

    private void updateIssueEntityFromRequest(IssueEntity issueEntity, IssueReqDTO issueReqDTO) {
        issueMapper.updateIssueEntity(issueEntity, issueReqDTO);

        updateAssignee(issueEntity, issueReqDTO.getAssigneeId());
        updateMilestone(issueEntity, issueReqDTO.getMilestoneId());
        updateIssueTag(issueEntity, issueReqDTO.getIssueTagId());
    }

    private void updateAssignee(IssueEntity issueEntity, Long assigneeId) {
        if (projectValidator.isValidAssigneeId(assigneeId)) {
            MemberEntity assignee = memberRepository.findById(assigneeId)
                    .orElseThrow(() -> MemberException.memberNotFoundException());
            issueEntity.setAssigneeEntity(assignee);
        } else {
            issueEntity.setAssigneeEntity(null);
        }
    }

    private void updateMilestone(IssueEntity issueEntity, Long milestoneId) {
        if (projectValidator.isValidMilestoneId(milestoneId)) {
            MilestoneEntity milestone = milestoneRepository.findById(milestoneId)
                    .orElseThrow(() -> MilestoneException.milestoneNotFoundException());
            issueEntity.setMilestoneEntity(milestone);
        } else {
            issueEntity.setMilestoneEntity(null);
        }
    }

    private void updateIssueTag(IssueEntity issueEntity, Long issueTagId) {
        if (issueTagId != null) {
            IssueTagEntity issueTag = issueTagRepository.findById(issueTagId)
                    .orElseThrow(() -> IssueException.issueTagNotFoundException());
            issueEntity.setIssueTagEntity(issueTag);
        } else {
            issueEntity.setIssueTagEntity(null);
        }
    }

    private void sendNotificationIfAssigneeChanged(IssueEntity savedIssueEntity, MemberEntity previousAssignee,
            Long orgId) {
        if (savedIssueEntity.getAssigneeEntity() != null &&
                (previousAssignee == null || !previousAssignee.getMemberId()
                        .equals(savedIssueEntity.getAssigneeEntity().getMemberId()))) {
            Long currentMemberId = SecurityUtil.getCurrentMemberIdByOrgId(orgId);
            sendIssueAssignedNotificationIfNeeded(savedIssueEntity, currentMemberId);
        }
    }

    private void validateProjectParticipantOrAdmin(Long orgId, Long projectId) {
        Long memberId = SecurityUtil.getCurrentMemberIdByOrgId(orgId);
        if (memberId == null) {
            throw MemberException.memberAccessDeniedException();
        }

        boolean isParticipant = projectParticipantService.isProjectParticipant(projectId, memberId);
        Role role = SecurityUtil.getCurrentRoleByOrgId(orgId);
        boolean isAdminOrRootAdmin = role != null && (role.equals(Role.ADMIN) || role.equals(Role.ROOT_ADMIN));

        if (!(isParticipant || isAdminOrRootAdmin)) {
            throw ProjectException.projectParticipantOrAdminOrRootAdminException();
        }
    }
}
