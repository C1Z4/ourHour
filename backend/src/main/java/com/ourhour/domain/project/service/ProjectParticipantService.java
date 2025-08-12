package com.ourhour.domain.project.service;

import com.ourhour.domain.project.entity.ProjectParticipantEntity;
import com.ourhour.domain.project.entity.ProjectParticipantId;
import com.ourhour.domain.project.mapper.ProjectParticipantMapper;
import com.ourhour.domain.project.repository.ProjectParticipantRepository;
import com.ourhour.domain.project.repository.ProjectRepository;
import com.ourhour.domain.org.exception.OrgException;
import com.ourhour.domain.org.repository.OrgRepository;
import com.ourhour.global.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ourhour.domain.project.dto.ProjectParticipantDTO;
import com.ourhour.domain.project.exception.ProjectException;
import com.ourhour.global.common.dto.ApiResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectParticipantService {

    private final ProjectParticipantRepository projectParticipantRepository;
    private final ProjectParticipantMapper projectParticipantMapper;
    private final ProjectRepository projectRepository;
    private final OrgRepository orgRepository;

    // 특정 프로젝트의 참가자 목록 조회
    public ApiResponse<PageResponse<ProjectParticipantDTO>> getProjectParticipants(Long projectId, Long orgId,
            Pageable pageable) {
        if (projectId <= 0) {
            throw ProjectException.projectNotFoundException();
        }

        // 프로젝트 존재 여부 확인
        if (!projectRepository.existsById(projectId)) {
            throw ProjectException.projectNotFoundException();
        }

        // 회사 존재 여부 확인
        if (!orgRepository.existsById(orgId)) {
            throw OrgException.orgNotFoundException();
        }

        Page<ProjectParticipantEntity> participantPage = projectParticipantRepository
                .findByProjectParticipantId_ProjectId(projectId,
                        pageable);

        if (participantPage.isEmpty()) {
            return ApiResponse.success(PageResponse.empty(pageable.getPageNumber(), pageable.getPageSize()));
        }

        Page<ProjectParticipantDTO> participantDTOPage = participantPage
                .map(entity -> projectParticipantMapper.toProjectParticipantDTO(entity, orgId));

        return ApiResponse.success(PageResponse.of(participantDTOPage), "프로젝트 참여자 목록 조회에 성공했습니다.");
    }

    public boolean isProjectParticipant(Long projectId, Long memberId) {
        ProjectParticipantId projectParticipantId = new ProjectParticipantId(projectId, memberId);
        return projectParticipantRepository.existsById(projectParticipantId);
    }

    // 프로젝트 참가자 삭제
    public ApiResponse<Void> deleteProjectParticipant(Long projectId, Long memberId) {
        if (projectId <= 0 || memberId <= 0) {
            throw ProjectException.projectNotFoundException();
        }

        if (!projectRepository.existsById(projectId)) {
            throw ProjectException.projectNotFoundException();
        }

        ProjectParticipantId projectParticipantId = new ProjectParticipantId(projectId, memberId);
        projectParticipantRepository.deleteById(projectParticipantId);

        return ApiResponse.success(null, "프로젝트 참가자 삭제에 성공했습니다.");
    }

}