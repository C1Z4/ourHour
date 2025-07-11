package com.ourhour.domain.project.service;

import com.ourhour.domain.project.dto.IssueDetailDTO;
import com.ourhour.domain.project.dto.IssueSummaryDTO;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.common.dto.PageResponse;
import com.ourhour.global.exception.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ourhour.domain.project.entity.IssueEntity;
import com.ourhour.domain.project.mapper.IssueMapper;
import com.ourhour.domain.project.repository.IssueRepository;
import com.ourhour.domain.project.repository.MilestoneRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final MilestoneRepository milestoneRepository;
    private final IssueMapper issueMapper;

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
}
