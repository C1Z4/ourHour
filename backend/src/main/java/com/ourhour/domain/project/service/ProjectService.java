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
    private final IssueRepository issueRepository;

    // 프로젝트 요약 목록 조회 - 페이징 처리
    public ApiResponse<PageResponse<ProjectSummaryResDTO>> getProjectsSummaryList(Long orgId, int participantLimit,
            boolean myProjectsOnly,
            Pageable pageable) {

        // 입력 검증
        validateProjectSummaryListParams(orgId, participantLimit);

        // 회사 존재 여부 확인
        if (!orgRepository.existsById(orgId)) {
            throw OrgException.orgNotFoundException();
        }

        // 프로젝트 페이지 조회
        Page<ProjectEntity> projectPage = getProjectPage(orgId, myProjectsOnly, pageable);

        if (projectPage.isEmpty()) {
            return ApiResponse.success(PageResponse.empty(pageable.getPageNumber() + 1, pageable.getPageSize()));
        }

        // 프로젝트 ID 목록 추출
        List<Long> projectIds = projectPage.getContent().stream()
                .map(ProjectEntity::getProjectId)
                .collect(Collectors.toList());

        // 모든 프로젝트의 참여자를 한 번에 조회
        List<ProjectParticipantEntity> allParticipants = projectParticipantRepository
                .findLimitedParticipantsByProjectIds(projectIds, participantLimit);

        // 프로젝트별 참여자 그룹화 후 제한된 수만큼만 유지
        Map<Long, List<ProjectParticipantEntity>> participantsByProject = allParticipants.stream()
                .collect(Collectors.groupingBy(p -> p.getProjectEntity().getProjectId()))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .limit(participantLimit)
                                .collect(Collectors.toList())));

        // DTO 변환
        Page<ProjectSummaryResDTO> projectSummaryPage = projectPage.map(project -> {
            ProjectSummaryResDTO projectSummary = projectMapper.toProjectSummaryResDTO(project);

            List<ProjectParticipantEntity> projectParticipants = participantsByProject
                    .getOrDefault(project.getProjectId(), List.of());

            List<ProjectSummaryParticipantDTO> participants = projectParticipants.stream()
                    .map(participant -> new ProjectSummaryParticipantDTO(
                            participant.getMemberEntity().getMemberId(),
                            participant.getMemberEntity().getName()))
                    .collect(Collectors.toList());

            projectSummary.setParticipants(participants);
            return projectSummary;
        });

        return ApiResponse.success(PageResponse.of(projectSummaryPage), "프로젝트 요약 목록 조회에 성공했습니다.");
    }

    private void validateProjectSummaryListParams(Long orgId, int participantLimit) {
        if (orgId == null || orgId <= 0) {
            throw OrgException.orgNotFoundException();
        }
        if (participantLimit <= 0) {
            throw ProjectException.projectParticipantLimitInvalidException();
        }
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

        return ApiResponse.success(projectInfo, "프로젝트 정보 조회에 성공했습니다.");
    }

    // 프로젝트 등록
    @Transactional
    public ApiResponse<Void> createProject(Long orgId, ProjectReqDTO projectReqDTO) {
        if (orgId <= 0) {
            throw OrgException.orgNotFoundException();
        }

        OrgEntity orgEntity = orgRepository.findById(orgId)
                .orElseThrow(() -> OrgException.orgNotFoundException());

        if (orgEntity == null) {
            throw OrgException.orgNotFoundException();
        }

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

        return ApiResponse.success(null, "프로젝트 등록이 완료되었습니다.");
    }

    // 프로젝트 수정(정보, 참가자)
    @Transactional
    public ApiResponse<Void> updateProject(Long projectId, ProjecUpdateReqDTO projectUpdateReqDTO) {
        if (projectId <= 0) {
            throw ProjectException.projectNotFoundException();
        }

        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> ProjectException.projectNotFoundException());

        projectMapper.updateProjectEntity(projectEntity, projectUpdateReqDTO);
        ProjectEntity savedProject = projectRepository.save(projectEntity);

        if (projectUpdateReqDTO.getParticipantIds() != null) {

            // 기존 참여자 모두 삭제
            projectParticipantRepository.deleteByProjectParticipantId_ProjectId(projectId);

            if (!projectUpdateReqDTO.getParticipantIds().isEmpty()) {
                List<ProjectParticipantEntity> newParticipants = projectUpdateReqDTO.getParticipantIds().stream()
                        .map(memberId -> {
                            if (!memberRepository.existsById(memberId)) {
                                throw MemberException.memberNotFoundException();
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
    public ApiResponse<PageResponse<MileStoneInfoDTO>> getProjectMilestones(Long projectId, boolean myMilestonesOnly,
            Pageable pageable) {
        if (projectId <= 0) {
            throw ProjectException.projectNotFoundException();
        }

        // 프로젝트 존재 여부 확인
        if (!projectRepository.existsById(projectId)) {
            throw ProjectException.projectNotFoundException();
        }

        Page<MilestoneEntity> milestonePage;

        if (myMilestonesOnly) {
            Long orgId = projectRepository.findById(projectId)
                    .orElseThrow(() -> ProjectException.projectNotFoundException())
                    .getOrgEntity()
                    .getOrgId();

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

        Page<MileStoneInfoDTO> milestoneInfoPage = milestonePage.map(milestone -> {
            int totalIssues = (int) issueRepository
                    .countByMilestoneEntity_MilestoneId(milestone.getMilestoneId());
            int completedIssues = (int) issueRepository.countByMilestoneEntity_MilestoneIdAndStatus(
                    milestone.getMilestoneId(), IssueStatus.COMPLETED);
            return new MileStoneInfoDTO(
                    milestone.getMilestoneId(),
                    milestone.getName(),
                    completedIssues,
                    totalIssues,
                    (byte) (totalIssues == 0 ? 0 : (completedIssues * 100 / totalIssues)));
        });

        PageResponse<MileStoneInfoDTO> response = PageResponse.of(milestoneInfoPage);

        String message = myMilestonesOnly
                ? "내가 할당된 이슈가 있는 마일스톤 목록 조회에 성공했습니다."
                : "특정 프로젝트의 마일스톤 목록 조회에 성공했습니다.";
        return ApiResponse.success(response, message);
    }

}