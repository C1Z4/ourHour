package com.ourhour.domain.project.service;

import com.ourhour.global.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ourhour.domain.member.exception.MemberException;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.project.dto.MilestoneReqDTO;
import com.ourhour.domain.project.entity.MilestoneEntity;
import com.ourhour.domain.project.entity.ProjectEntity;
import com.ourhour.domain.project.enums.SyncOperation;
import com.ourhour.domain.project.mapper.MilestoneMapper;
import com.ourhour.domain.project.repository.MilestoneRepository;
import com.ourhour.domain.project.repository.ProjectRepository;
import com.ourhour.domain.project.exception.ProjectException;
import com.ourhour.domain.project.exception.MilestoneException;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.domain.project.annotation.GitHubSync;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;
    private final MilestoneMapper milestoneMapper;

    // 마일스톤 등록
    @GitHubSync(operation = SyncOperation.CREATE)
    @Transactional
    public ApiResponse<Void> createMilestone(Long projectId, MilestoneReqDTO milestoneReqDTO) {
        if (projectId <= 0) {
            throw ProjectException.projectNotFoundException();
        }

        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> ProjectException.projectNotFoundException());

        if (projectEntity == null) {
            throw ProjectException.projectNotFoundException();
        }

        // 마일스톤 이름 중복 체크
        if (milestoneRepository.findByProjectEntity_ProjectIdAndName(projectId, milestoneReqDTO.getName())
                .isPresent()) {
            throw MilestoneException.milestoneNameDuplicateException();
        }

        MilestoneEntity milestoneEntity = milestoneMapper.toMilestoneEntity(projectEntity, milestoneReqDTO);

        milestoneRepository.save(milestoneEntity);

        return ApiResponse.success(null, "마일스톤 등록에 성공했습니다.");
    }

    // 마일스톤 수정
    @GitHubSync(operation = SyncOperation.UPDATE)
    @Transactional
    public ApiResponse<Void> updateMilestone(Long milestoneId, MilestoneReqDTO milestoneReqDTO) {
        if (milestoneId <= 0) {
            throw MilestoneException.milestoneNotFoundException();
        }

        MilestoneEntity milestoneEntity = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> MilestoneException.milestoneNotFoundException());

        milestoneMapper.updateMilestoneEntity(milestoneEntity, milestoneReqDTO);

        milestoneRepository.save(milestoneEntity);

        return ApiResponse.success(null, "마일스톤 수정에 성공했습니다.");
    }

    // 마일스톤 삭제
    @GitHubSync(operation = SyncOperation.DELETE, entityParam = "milestoneId")
    @Transactional
    public ApiResponse<Void> deleteMilestone(Long orgId, Long milestoneId) {
        if (milestoneId <= 0) {
            throw MilestoneException.milestoneNotFoundException();
        }

        MilestoneEntity milestoneEntity = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> MilestoneException.milestoneNotFoundException());

        // 권한 검사: 해당 회사의 멤버인지 확인
        Long memberId = SecurityUtil.getCurrentMemberIdByOrgId(orgId);
        if (memberId == null) {
            throw MemberException.memberAccessDeniedException();
        }

        // 권한 검사: 프로젝트 참여자이거나 ADMIN 이상 권한이 있는지 확인
        Role role = SecurityUtil.getCurrentRoleByOrgId(orgId);
        boolean isAdminOrRootAdmin = role != null && (role.equals(Role.ADMIN) || role.equals(Role.ROOT_ADMIN));
        if (!isAdminOrRootAdmin) {
            throw ProjectException.projectParticipantOrAdminOrRootAdminException();
        }

        milestoneRepository.delete(milestoneEntity);

        return ApiResponse.success(null, "마일스톤 삭제에 성공했습니다.");
    }

}
