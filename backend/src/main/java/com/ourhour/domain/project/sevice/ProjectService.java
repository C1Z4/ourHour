package com.ourhour.domain.project.sevice;

import com.ourhour.domain.org.repository.OrgRepository;
import com.ourhour.domain.project.dto.ProjectSummaryParticipantDTO;
import com.ourhour.domain.project.dto.ProjectSummaryResDTO;
import com.ourhour.domain.project.entity.ProjectEntity;
import com.ourhour.domain.project.entity.ProjectParticipantEntity;
import com.ourhour.domain.project.mapper.ProjectMapper;
import com.ourhour.domain.project.repository.ProjectParticipantRepository;
import com.ourhour.domain.project.repository.ProjectRepository;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.common.dto.PageResponse;
import com.ourhour.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectParticipantRepository projectParticipantRepository;
    private final OrgRepository orgRepository;
    private final ProjectMapper projectMapper;

    // 프로젝트 요약 목록 조회 - 페이징 처리
    public ApiResponse<PageResponse<ProjectSummaryResDTO>> getProjectsSummaryList(Long orgId, int participantLimit,
            Pageable pageable) {

        if (orgId <= 0) {
            throw BusinessException.badRequest("유효하지 않은 회사 ID입니다.");
        }

        if (participantLimit <= 0) {
            throw BusinessException.badRequest("참여자 제한 수는 1 이상이어야 합니다.");
        }

        // 회사 존재 여부 확인
        if (!orgRepository.existsById(orgId)) {
            throw BusinessException.badRequest("존재하지 않는 회사 ID입니다.");
        }

        Page<ProjectEntity> projectPage = projectRepository.findByOrgEntity_OrgId(orgId, pageable);

        if (projectPage.isEmpty()) {
            return ApiResponse.success(PageResponse.empty(pageable.getPageNumber(), pageable.getPageSize()));
        }

        Page<ProjectSummaryResDTO> projectSummaryPage = projectPage.map(project -> {
            ProjectSummaryResDTO projectSummary = projectMapper.toProjectSummaryResDTO(project);

            List<ProjectParticipantEntity> participantEntities = projectParticipantRepository
                    .findLimitedParticipants(project.getProjectId(), participantLimit);

            List<ProjectSummaryParticipantDTO> participants = participantEntities.stream()
                    .map(participant -> new ProjectSummaryParticipantDTO(
                            participant.getMemberEntity().getMemberId(),
                            participant.getMemberEntity().getName()))
                    .collect(Collectors.toList());

            projectSummary.setParticipants(participants);
            return projectSummary;
        });

        return ApiResponse.success(PageResponse.of(projectSummaryPage), "프로젝트 요약 목록 조회에 성공했습니다.");
    }

}