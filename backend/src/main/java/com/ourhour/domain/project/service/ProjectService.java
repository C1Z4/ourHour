package com.ourhour.domain.project.service;

import com.ourhour.domain.org.repository.OrgRepository;
import com.ourhour.domain.project.dto.ProjecUpdateReqDTO;
import com.ourhour.domain.project.dto.MileStoneInfoDTO;
import com.ourhour.domain.project.dto.ProjectInfoDTO;
import com.ourhour.domain.project.dto.ProjectSummaryParticipantDTO;
import com.ourhour.domain.project.dto.ProjectSummaryResDTO;
import com.ourhour.domain.project.entity.MilestoneEntity;
import com.ourhour.domain.project.dto.ProjectReqDTO;
import com.ourhour.domain.project.entity.ProjectEntity;
import com.ourhour.domain.project.entity.ProjectParticipantEntity;
import com.ourhour.domain.project.mapper.MilestoneMapper;
import com.ourhour.domain.project.mapper.ProjectMapper;
import com.ourhour.domain.project.repository.MilestoneRepository;
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
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.project.entity.ProjectParticipantId;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectParticipantRepository projectParticipantRepository;
    private final OrgRepository orgRepository;
    private final ProjectMapper projectMapper;
    private final MemberRepository memberRepository;

    private final MilestoneRepository milestoneRepository;
    private final MilestoneMapper milestoneMapper;

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

    // 프로젝트 정보 조회
    public ApiResponse<ProjectInfoDTO> getProjectInfo(Long projectId) {
        if (projectId <= 0) {
            throw BusinessException.badRequest("유효하지 않은 프로젝트 ID입니다.");
        }

        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> BusinessException.badRequest("존재하지 않는 프로젝트 ID입니다."));

        ProjectInfoDTO projectInfo = projectMapper.toProjectInfoDTO(project);

        return ApiResponse.success(projectInfo, "프로젝트 정보 조회에 성공했습니다.");
    }

    // 프로젝트 등록
    public ApiResponse<Void> createProject(Long orgId, ProjectReqDTO projectReqDTO) {
        if (orgId <= 0) {
            throw BusinessException.badRequest("유효하지 않은 조직 ID입니다.");
        }

        OrgEntity orgEntity = orgRepository.findById(orgId)
                .orElseThrow(() -> BusinessException.badRequest("존재하지 않는 조직 ID입니다."));

        if (orgEntity == null) {
            throw BusinessException.badRequest("조직 정보를 찾을 수 없습니다.");
        }

        ProjectEntity projectEntity = projectMapper.toProjectEntity(orgEntity, projectReqDTO);
        
        projectRepository.save(projectEntity);

        return ApiResponse.success(null, "프로젝트 등록이 완료되었습니다.");
    }

    // 프로젝트 수정(정보, 참가자)
    @Transactional
    public ApiResponse<Void> updateProject(Long projectId, ProjecUpdateReqDTO projectUpdateReqDTO) {
        if (projectId <= 0) {
            throw BusinessException.badRequest("유효하지 않은 프로젝트 ID입니다.");
        }

        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> BusinessException.badRequest("존재하지 않는 프로젝트 ID입니다."));

        projectMapper.updateProjectEntity(projectEntity, projectUpdateReqDTO);
        ProjectEntity savedProject = projectRepository.save(projectEntity);

        if (projectUpdateReqDTO.getParticipantIds() != null) {

            // 기존 참여자 모두 삭제
            projectParticipantRepository.deleteByProjectParticipantId_ProjectId(projectId);

            if (!projectUpdateReqDTO.getParticipantIds().isEmpty()) {
                List<ProjectParticipantEntity> newParticipants = projectUpdateReqDTO.getParticipantIds().stream()
                        .map(memberId -> {
                            if (!memberRepository.existsById(memberId)) {
                                throw BusinessException.badRequest("존재하지 않는 멤버 ID입니다: " + memberId);
                            }

                            ProjectParticipantId participantId = new ProjectParticipantId(projectId, memberId);

                            return ProjectParticipantEntity.builder()
                                    .projectParticipantId(participantId)
                                    .projectEntity(savedProject)
                                    .memberEntity(memberRepository.getReferenceById(memberId)) // 실제 필드값이 필요하지 않아
                                                                                               // reference(단순 참조, 지연로딩)
                                    .build();
                        })
                        .collect(Collectors.toList());

                projectParticipantRepository.saveAll(newParticipants);
            }
        }

        return ApiResponse.success(null, "프로젝트 수정이 완료되었습니다.");
    }

    // 프로젝트 삭제
    public ApiResponse<Void> deleteProject(Long projectId) {
        projectRepository.deleteById(projectId);
        return ApiResponse.success(null, "프로젝트 삭제가 완료되었습니다.");
    }

    // 특정 프로젝트의 마일스톤 목록 조회
    public ApiResponse<PageResponse<MileStoneInfoDTO>> getProjectMilestones(Long projectId, Pageable pageable) {
        if (projectId <= 0) {
            throw BusinessException.badRequest("유효하지 않은 프로젝트 ID입니다.");
        }

        // 프로젝트 존재 여부 확인
        if (!projectRepository.existsById(projectId)) {
            throw BusinessException.badRequest("존재하지 않는 프로젝트 ID입니다.");
        }

        Page<MilestoneEntity> milestonePage = milestoneRepository.findByProjectEntity_ProjectId(projectId, pageable);

        if (milestonePage.isEmpty()) {
            return ApiResponse.success(PageResponse.empty(pageable.getPageNumber(), pageable.getPageSize()));
        }

        Page<MileStoneInfoDTO> milestoneInfoPage = milestonePage.map(milestoneMapper::toMileStoneInfoDTO);

        return ApiResponse.success(PageResponse.of(milestoneInfoPage), "특정 프로젝트의 마일스톤 목록 조회에 성공했습니다.");
    }

}