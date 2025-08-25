package com.ourhour.domain.project.service;

import java.util.List;
import java.util.stream.Collectors;

import com.ourhour.domain.board.exception.PostException;
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

    // 특정 마일스톤의 이슈 목록 조회 (milestoneId가 null이면 마일스톤이 할당되지 않은 이슈들 조회)
    public ApiResponse<PageResponse<IssueSummaryDTO>> getMilestoneIssues(Long projectId, Long milestoneId,
            boolean myIssuesOnly, Pageable pageable) {
        if (projectId <= 0) {
            throw ProjectException.projectNotFoundException();
        }

        if (!projectRepository.existsById(projectId)) {
            throw ProjectException.projectNotFoundException();
        }

        Long orgId = projectRepository.findById(projectId)
                .orElseThrow(() -> ProjectException.projectNotFoundException())
                .getOrgEntity()
                .getOrgId();

        if (myIssuesOnly) {
            Long memberId = SecurityUtil.getCurrentMemberIdByOrgId(orgId);
            if (memberId == null) {
                throw MemberException.memberAccessDeniedException();
            }

            Page<IssueEntity> issuePage;

            if (milestoneId != null && milestoneId > 0) {
                // 특정 마일스톤의 내가 할당된 이슈 조회
                if (!milestoneRepository.existsById(milestoneId)) {
                    throw MilestoneException.milestoneNotFoundException();
                }
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

            String message = milestoneId != null && milestoneId > 0
                    ? "특정 마일스톤의 내가 할당된 이슈 목록 조회에 성공했습니다."
                    : "마일스톤이 할당되지 않은 내가 할당된 이슈 목록 조회에 성공했습니다.";

            return ApiResponse.success(PageResponse.of(issuePage.map(issueMapper::toIssueSummaryDTO)), message);
        }

        Page<IssueEntity> issuePage;

        if (milestoneId != null && milestoneId > 0) {
            // 특정 마일스톤의 이슈 조회
            if (!milestoneRepository.existsById(milestoneId)) {
                throw MilestoneException.milestoneNotFoundException();
            }
            issuePage = issueRepository.findByMilestoneEntity_MilestoneId(milestoneId, pageable);
        } else {
            // 마일스톤이 할당되지 않은 이슈들 조회
            issuePage = issueRepository.findByProjectEntity_ProjectIdAndMilestoneEntityIsNull(projectId, pageable);
        }

        if (issuePage.isEmpty()) {
            return ApiResponse.success(PageResponse.empty(pageable.getPageNumber() + 1, pageable.getPageSize()));
        }

        Page<IssueSummaryDTO> issueDTOPage = issuePage.map(issueMapper::toIssueSummaryDTO);

        String message = milestoneId != null && milestoneId > 0
                ? "특정 마일스톤의 이슈 목록 조회에 성공했습니다."
                : "마일스톤이 할당되지 않은 이슈 목록 조회에 성공했습니다.";

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

        return ApiResponse.success(issueDetailDTO, "이슈 상세 조회에 성공했습니다.");
    }

    // 이슈 등록
    @GitHubSync(operation = SyncOperation.CREATE)
    @Transactional
    public ApiResponse<IssueDetailDTO> createIssue(Long projectId, IssueReqDTO issueReqDTO) {
        if (projectId <= 0) {
            throw ProjectException.projectNotFoundException();
        }

        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> ProjectException.projectNotFoundException());

        IssueEntity issueEntity = issueMapper.toIssueEntity(issueReqDTO);

        issueEntity.setProjectEntity(projectEntity);

        // 마일스톤 설정
        if (issueReqDTO.getMilestoneId() != null && issueReqDTO.getMilestoneId() > 0) {
            MilestoneEntity milestoneEntity = milestoneRepository.findById(issueReqDTO.getMilestoneId())
                    .orElseThrow(() -> MilestoneException.milestoneNotFoundException());
            issueEntity.setMilestoneEntity(milestoneEntity);
        }

        // 담당자 설정
        if (issueReqDTO.getAssigneeId() != null && issueReqDTO.getAssigneeId() > 0) {
            MemberEntity assigneeEntity = memberRepository.findById(issueReqDTO.getAssigneeId())
                    .orElseThrow(() -> MemberException.memberNotFoundException());
            issueEntity.setAssigneeEntity(assigneeEntity);
        }

        IssueEntity savedIssueEntity = issueRepository.save(issueEntity);

        IssueDetailDTO issueDetailDTO = issueMapper.toIssueDetailDTO(savedIssueEntity);

        return ApiResponse.success(issueDetailDTO, "이슈 등록에 성공했습니다.");
    }

    // 이슈 수정
    @GitHubSync(operation = SyncOperation.UPDATE)
    @Transactional
    public ApiResponse<IssueDetailDTO> updateIssue(Long orgId, Long issueId, IssueReqDTO issueReqDTO) {
        if (issueId <= 0) {
            throw IssueException.issueNotFoundException();
        }

        IssueEntity issueEntity = issueRepository.findById(issueId)
                .orElseThrow(() -> IssueException.issueNotFoundException());

        Long projectId = issueEntity.getProjectEntity().getProjectId();

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

        issueMapper.updateIssueEntity(issueEntity, issueReqDTO);

        if (issueReqDTO.getAssigneeId() != null) {
            MemberEntity assignee = memberRepository.findById(issueReqDTO.getAssigneeId())
                    .orElseThrow(() -> MemberException.memberNotFoundException());
            issueEntity.setAssigneeEntity(assignee);
        } else {
            issueEntity.setAssigneeEntity(null);
        }

        if (issueReqDTO.getMilestoneId() != null) {
            MilestoneEntity milestone = milestoneRepository.findById(issueReqDTO.getMilestoneId())
                    .orElseThrow(() -> MilestoneException.milestoneNotFoundException());
            issueEntity.setMilestoneEntity(milestone);
        } else {
            issueEntity.setMilestoneEntity(null);
        }

        // 태그 설정
        if (issueReqDTO.getIssueTagId() != null) {
            IssueTagEntity issueTag = issueTagRepository.findById(issueReqDTO.getIssueTagId())
                    .orElseThrow(() -> IssueException.issueTagNotFoundException());
            issueEntity.setIssueTagEntity(issueTag);
        } else {
            issueEntity.setIssueTagEntity(null);
        }

        IssueEntity savedIssueEntity = issueRepository.save(issueEntity);

        IssueDetailDTO issueDetailDTO = issueMapper.toIssueDetailDTO(savedIssueEntity);

        return ApiResponse.success(issueDetailDTO, "이슈 수정에 성공했습니다.");
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

        return ApiResponse.success(issueDetailDTO, "이슈 상태 수정에 성공했습니다.");
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

        Long projectId = issueEntity.getProjectEntity().getProjectId();

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

        issueRepository.deleteById(issueId);

        return ApiResponse.success(null, "이슈 삭제에 성공했습니다.");
    }

    // 이슈 태그 조회
    public ApiResponse<List<IssueTagDTO>> getIssueTags(Long projectId) {
        if (projectId <= 0) {
            throw ProjectException.projectNotFoundException();
        }

        List<IssueTagEntity> issueTagEntities = issueTagRepository.findByProjectEntity_ProjectId(projectId);

        if (issueTagEntities.isEmpty()) {
            return ApiResponse.success(null, "이슈 태그가 존재하지 않습니다.");
        }

        return ApiResponse.success(issueTagEntities.stream()
                .map(issueTagMapper::toIssueTagDTO)
                .collect(Collectors.toList()), "이슈 태그 조회에 성공했습니다.");
    }

    // 이슈 태그 등록
    @Transactional
    public ApiResponse<Void> createIssueTag(Long projectId, IssueTagDTO issueTagDTO) {
        if (projectId <= 0) {
            throw ProjectException.projectNotFoundException();
        }

        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> ProjectException.projectNotFoundException());

        IssueTagEntity issueTagEntity = issueTagMapper.toIssueTagEntity(issueTagDTO);

        issueTagEntity.setProjectEntity(projectEntity);

        issueTagRepository.save(issueTagEntity);

        return ApiResponse.success(null, "이슈 태그 등록에 성공했습니다.");
    }

    // 이슈 태그 수정
    @Transactional
    public ApiResponse<Void> updateIssueTag(Long projectId, Long issueTagId, IssueTagDTO issueTagDTO) {
        if (projectId <= 0) {
            throw ProjectException.projectNotFoundException();
        }

        if (issueTagId <= 0) {
            throw IssueException.issueNotFoundException();
        }

        IssueTagEntity issueTagEntity = issueTagRepository.findById(issueTagId)
                .orElseThrow(() -> IssueException.issueTagNotFoundException());

        issueTagMapper.updateIssueTagEntity(issueTagEntity, issueTagDTO);

        return ApiResponse.success(null, "이슈 태그 수정에 성공했습니다.");
    }

    // 이슈 태그 삭제
    @Transactional
    public ApiResponse<Void> deleteIssueTag(Long projectId, Long issueTagId) {
        if (projectId <= 0) {
            throw ProjectException.projectNotFoundException();
        }

        if (issueTagId <= 0) {
            throw IssueException.issueNotFoundException();
        }

        IssueTagEntity issueTagEntity = issueTagRepository.findById(issueTagId)
                .orElseThrow(() -> IssueException.issueTagNotFoundException());

        issueTagRepository.delete(issueTagEntity);

        return ApiResponse.success(null, "이슈 태그 삭제에 성공했습니다.");
    }
}
