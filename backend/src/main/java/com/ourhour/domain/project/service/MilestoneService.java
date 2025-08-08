package com.ourhour.domain.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ourhour.domain.member.exception.MemberException;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.project.dto.MilestoneReqDTO;
import com.ourhour.domain.project.entity.MilestoneEntity;
import com.ourhour.domain.project.entity.ProjectEntity;
import com.ourhour.domain.project.mapper.MilestoneMapper;
import com.ourhour.domain.project.repository.MilestoneRepository;
import com.ourhour.domain.project.repository.ProjectRepository;
import com.ourhour.domain.project.exception.ProjectException;
import com.ourhour.domain.project.exception.MilestoneException;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.jwt.dto.Claims;

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
    @Transactional
    public ApiResponse<Void> deleteMilestone(Long milestoneId, Claims claims) {
        if (milestoneId <= 0) {
            throw MilestoneException.milestoneNotFoundException();
        }

        MilestoneEntity milestoneEntity = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> MilestoneException.milestoneNotFoundException());

        Long orgId = milestoneEntity.getProjectEntity().getOrgEntity().getOrgId();

        // 권한 검사: 해당 회사의 멤버인지 확인
        boolean isOrgMember = claims.getOrgAuthorityList().stream()
                .anyMatch(orgAuthority -> orgAuthority.getOrgId().equals(orgId));

        if (!isOrgMember) {
            throw MemberException.memberAccessDeniedException();
        }

        // 권한 검사: 프로젝트 참여자이거나 ADMIN 이상 권한이 있는지 확인
        boolean hasPermission = claims.getOrgAuthorityList().stream()
                .anyMatch(orgAuthority -> orgAuthority.getOrgId().equals(orgId) &&
                        (orgAuthority.getRole().equals(Role.ADMIN) || orgAuthority.getRole().equals(Role.ROOT_ADMIN)));

        if (!hasPermission) {
            throw ProjectException.projectParticipantOrAdminOrRootAdminException();
        }

        milestoneRepository.delete(milestoneEntity);

        return ApiResponse.success(null, "마일스톤 삭제에 성공했습니다.");
    }

}
