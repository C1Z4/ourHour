package com.ourhour.domain.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.project.dto.MilestoneReqDTO;
import com.ourhour.domain.project.entity.MilestoneEntity;
import com.ourhour.domain.project.entity.ProjectEntity;
import com.ourhour.domain.project.mapper.MilestoneMapper;
import com.ourhour.domain.project.repository.MilestoneRepository;
import com.ourhour.domain.project.repository.ProjectRepository;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.exception.BusinessException;
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
    private final ProjectParticipantService projectParticipantService;

    // 마일스톤 등록
    @Transactional
    public ApiResponse<Void> createMilestone(Long projectId, MilestoneReqDTO milestoneReqDTO) {
        if (projectId <= 0) {
            throw BusinessException.badRequest("유효하지 않은 프로젝트 ID입니다.");
        }

        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> BusinessException.badRequest("존재하지 않는 프로젝트 ID입니다."));

        if (projectEntity == null) {
            throw BusinessException.badRequest("프로젝트 정보를 찾을 수 없습니다.");
        }

        // 마일스톤 이름 중복 체크
        if (milestoneRepository.findByProjectEntity_ProjectIdAndName(projectId, milestoneReqDTO.getName())
                .isPresent()) {
            throw BusinessException.badRequest("이미 존재하는 마일스톤 이름입니다.");
        }

        MilestoneEntity milestoneEntity = milestoneMapper.toMilestoneEntity(projectEntity, milestoneReqDTO);

        milestoneRepository.save(milestoneEntity);

        return ApiResponse.success(null, "마일스톤 등록에 성공했습니다.");
    }

    // 마일스톤 수정(마일스톤 이름)
    @Transactional
    public ApiResponse<Void> updateMilestone(Long milestoneId, MilestoneReqDTO milestoneReqDTO) {
        MilestoneEntity milestoneEntity = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> BusinessException.badRequest("존재하지 않는 마일스톤 ID입니다."));

        milestoneMapper.updateMilestoneEntity(milestoneEntity, milestoneReqDTO);

        milestoneRepository.save(milestoneEntity);

        return ApiResponse.success(null, "마일스톤 수정에 성공했습니다.");
    }

    // 마일스톤 삭제
    @Transactional
    public ApiResponse<Void> deleteMilestone(Long milestoneId, Claims claims) {

        MilestoneEntity milestoneEntity = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> BusinessException.badRequest("존재하지 않는 마일스톤 ID입니다."));

        Long orgId = milestoneEntity.getProjectEntity().getOrgEntity().getOrgId();
        Long projectId = milestoneEntity.getProjectEntity().getProjectId();

        Long memberId = claims.getOrgAuthorityList().stream()       
                                .filter(auth -> auth.getOrgId().equals(orgId))
                                .map(auth -> auth.getMemberId())
                                .findFirst()
        .orElseThrow(() -> BusinessException.forbidden("해당 회사의 멤버가 아닙니다."));

        boolean isParticipant = projectParticipantService.isProjectParticipant(projectId, memberId);

        boolean isAdminOrRootAdmin = claims.getOrgAuthorityList().stream()
                                .filter(auth -> auth.getOrgId().equals(orgId))
                                .anyMatch(auth -> auth.getRole().equals(Role.ADMIN) || auth.getRole().equals(Role.ROOT_ADMIN));

        if (!(isParticipant || isAdminOrRootAdmin)) {
                throw BusinessException.forbidden("프로젝트 참여자이거나 ADMIN 이상 권한이 있어야 합니다.");
        }

        milestoneRepository.delete(milestoneEntity);

        return ApiResponse.success(null, "마일스톤 삭제에 성공했습니다.");
    }

}
