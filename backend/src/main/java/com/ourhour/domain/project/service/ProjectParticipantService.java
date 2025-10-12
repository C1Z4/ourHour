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
import com.ourhour.domain.project.entity.ProjectEntity;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.member.exception.MemberException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectParticipantService {

    private final ProjectParticipantRepository projectParticipantRepository;
    private final ProjectParticipantMapper projectParticipantMapper;
    private final ProjectRepository projectRepository;
    private final OrgRepository orgRepository;
    private final MemberRepository memberRepository;

    // 특정 프로젝트의 참가자 목록 조회
    public ApiResponse<PageResponse<ProjectParticipantDTO>> getProjectParticipants(Long projectId, Long orgId,
            String search, Pageable pageable) {
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

        Page<ProjectParticipantEntity> participantPage;
        if (search != null && !search.trim().isEmpty()) {
            participantPage = projectParticipantRepository
                    .findByProjectParticipantId_ProjectIdAndMemberNameContaining(projectId, search.trim(), pageable);
        } else {
            participantPage = projectParticipantRepository
                    .findByProjectParticipantId_ProjectId(projectId, pageable);
        }

        if (participantPage.isEmpty()) {
            return ApiResponse.success(PageResponse.empty(pageable.getPageNumber() + 1, pageable.getPageSize()));
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

    // 프로젝트 참여자 업데이트
    @Transactional
    public void updateProjectParticipants(Long projectId, List<Long> participantIds, ProjectEntity savedProject) {
        if (participantIds == null) {
            return;
        }

        // 기존 참여자 모두 삭제
        projectParticipantRepository.deleteByProjectParticipantId_ProjectId(projectId);

        if (!participantIds.isEmpty()) {
            List<ProjectParticipantEntity> newParticipants = createNewParticipants(projectId, participantIds,
                    savedProject);
            projectParticipantRepository.saveAll(newParticipants);
        }
    }

    private List<ProjectParticipantEntity> createNewParticipants(Long projectId, List<Long> participantIds,
            ProjectEntity savedProject) {
        return participantIds.stream()
                .map(memberId -> {
                    validateMemberExists(memberId);
                    ProjectParticipantId participantId = new ProjectParticipantId(projectId, memberId);

                    return ProjectParticipantEntity.builder()
                            .projectParticipantId(participantId)
                            .projectEntity(savedProject)
                            .memberEntity(memberRepository.getReferenceById(memberId))
                            .build();
                })
                .toList();
    }

    private void validateMemberExists(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw MemberException.memberNotFoundException();
        }
    }

}