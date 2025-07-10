package com.ourhour.domain.project.service;

import com.ourhour.domain.project.entity.ProjectParticipantEntity;
import com.ourhour.domain.project.mapper.ProjectParticipantMapper;
import com.ourhour.domain.project.repository.ProjectParticipantRepository;
import com.ourhour.domain.project.repository.ProjectRepository;
import com.ourhour.global.common.dto.PageResponse;
import com.ourhour.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ourhour.domain.project.dto.ProjectParticipantDTO;
import com.ourhour.global.common.dto.ApiResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectParticipantService {

    private final ProjectParticipantRepository projectParticipantRepository;
    private final ProjectParticipantMapper projectParticipantMapper;
    private final ProjectRepository projectRepository;

    public ApiResponse<PageResponse<ProjectParticipantDTO>> getProjectParticipants(Long projectId, Pageable pageable) {
        if (projectId <= 0) {
            throw BusinessException.badRequest("유효하지 않은 프로젝트 ID입니다.");
        }

        // 프로젝트 존재 여부 확인
        if (!projectRepository.existsById(projectId)) {
            throw BusinessException.badRequest("존재하지 않는 프로젝트 ID입니다.");
        }

        Page<ProjectParticipantEntity> participantPage = projectParticipantRepository.findByProjectParticipantId_ProjectId(projectId,
                pageable);

        if (participantPage.isEmpty()) {
            return ApiResponse.success(PageResponse.empty(pageable.getPageNumber(), pageable.getPageSize()));
        }

        Page<ProjectParticipantDTO> participantDTOPage = participantPage
                .map(projectParticipantMapper::toProjectParticipantDTO);

        return ApiResponse.success(PageResponse.of(participantDTOPage));
    }

}