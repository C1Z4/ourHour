package com.ourhour.domain.project.service;

import com.ourhour.domain.project.dto.IssueDetailDTO;
import com.ourhour.domain.project.dto.IssueSummaryDTO;
import com.ourhour.domain.project.dto.IssueReqDTO;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.common.dto.PageResponse;
import com.ourhour.global.exception.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ourhour.domain.project.entity.IssueEntity;
import com.ourhour.domain.project.entity.MilestoneEntity;
import com.ourhour.domain.project.entity.ProjectEntity;
import com.ourhour.domain.project.mapper.IssueMapper;
import com.ourhour.domain.project.repository.IssueRepository;
import com.ourhour.domain.project.repository.ProjectRepository;
import com.ourhour.domain.project.repository.MilestoneRepository;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.repository.MemberRepository;

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

    // 특정 마일스톤의 이슈 목록 조회
    public ApiResponse<PageResponse<IssueSummaryDTO>> getMilestoneIssues(Long milestoneId, Pageable pageable) {
        if (milestoneId <= 0) {
            throw BusinessException.badRequest("유효하지 않은 마일스톤 ID입니다.");
        }

        if (!milestoneRepository.existsById(milestoneId)) {
            throw BusinessException.badRequest("존재하지 않는 마일스톤 ID입니다.");
        }

        Page<IssueEntity> issuePage = issueRepository.findByMilestoneEntity_MilestoneId(milestoneId, pageable);

        if (issuePage.isEmpty()) {
            return ApiResponse.success(PageResponse.empty(pageable.getPageNumber(), pageable.getPageSize()));
        }

        Page<IssueSummaryDTO> issueDTOPage = issuePage.map(issueMapper::toIssueSummaryDTO);

        return ApiResponse.success(PageResponse.of(issueDTOPage), "특정 마일스톤의 이슈 목록 조회에 성공했습니다.");
    }

    // 이슈 상세 조회
    public ApiResponse<IssueDetailDTO> getIssueDetail(Long issueId) {
        if (issueId <= 0) {
            throw BusinessException.badRequest("유효하지 않은 이슈 ID입니다.");
        }

        IssueEntity issueEntity = issueRepository.findById(issueId)
                .orElseThrow(() -> BusinessException.badRequest("존재하지 않는 이슈 ID입니다."));

        IssueDetailDTO issueDetailDTO = issueMapper.toIssueDetailDTO(issueEntity);

        return ApiResponse.success(issueDetailDTO, "이슈 상세 조회에 성공했습니다.");
    }

    // 이슈 등록
    @Transactional
    public ApiResponse<IssueDetailDTO> createIssue(Long projectId, IssueReqDTO issueReqDTO) {
        if (projectId <= 0) {
            throw BusinessException.badRequest("유효하지 않은 프로젝트 ID입니다.");
        }

        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> BusinessException.badRequest("존재하지 않는 프로젝트 ID입니다."));


        IssueEntity issueEntity = issueMapper.toIssueEntity(issueReqDTO);

        issueEntity.setProjectEntity(projectEntity);

        // 마일스톤 설정
        if (issueReqDTO.getMilestoneId() != null && issueReqDTO.getMilestoneId() > 0) {
            MilestoneEntity milestoneEntity = milestoneRepository.findById(issueReqDTO.getMilestoneId())
                    .orElseThrow(() -> BusinessException.badRequest("존재하지 않는 마일스톤 ID입니다."));
            issueEntity.setMilestoneEntity(milestoneEntity);
        } else {
            MilestoneEntity unclassifiedMilestone = milestoneRepository
                    .findByProjectEntity_ProjectIdAndName(projectId, "미분류")
                    .orElseThrow(() -> BusinessException.badRequest("해당 프로젝트에 '미분류' 마일스톤이 존재하지 않습니다."));
            issueEntity.setMilestoneEntity(unclassifiedMilestone);
        }

        // 담당자 설정
        if (issueReqDTO.getAssigneeId() != null && issueReqDTO.getAssigneeId() > 0) {
            MemberEntity assigneeEntity = memberRepository.findById(issueReqDTO.getAssigneeId())
                    .orElseThrow(() -> BusinessException.badRequest("존재하지 않는 담당자 ID입니다."));
            issueEntity.setAssigneeEntity(assigneeEntity);
        }

        IssueEntity savedIssueEntity = issueRepository.save(issueEntity);

        IssueDetailDTO issueDetailDTO = issueMapper.toIssueDetailDTO(savedIssueEntity);

        return ApiResponse.success(issueDetailDTO, "이슈 등록에 성공했습니다.");
    }

    // 이슈 수정
    @Transactional
    public ApiResponse<IssueDetailDTO> updateIssue(Long issueId, IssueReqDTO issueReqDTO) {
        if (issueId <= 0) {
            throw BusinessException.badRequest("유효하지 않은 이슈 ID입니다.");
        }

        IssueEntity issueEntity = issueRepository.findById(issueId)
                .orElseThrow(() -> BusinessException.badRequest("존재하지 않는 이슈 ID입니다."));

        issueMapper.updateIssueEntity(issueEntity, issueReqDTO);

        IssueEntity savedIssueEntity = issueRepository.save(issueEntity);

        IssueDetailDTO issueDetailDTO = issueMapper.toIssueDetailDTO(savedIssueEntity);

        return ApiResponse.success(issueDetailDTO, "이슈 수정에 성공했습니다.");
    }

    // 이슈 삭제
    @Transactional
    public ApiResponse<Void> deleteIssue(Long issueId) {
        if (issueId <= 0) {
            throw BusinessException.badRequest("유효하지 않은 이슈 ID입니다.");
        }

        issueRepository.deleteById(issueId);

        return ApiResponse.success(null, "이슈 삭제에 성공했습니다.");
    }
}
