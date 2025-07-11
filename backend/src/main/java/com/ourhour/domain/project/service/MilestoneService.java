package com.ourhour.domain.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ourhour.domain.project.dto.MilestoneReqDTO;
import com.ourhour.domain.project.entity.MilestoneEntity;
import com.ourhour.domain.project.entity.ProjectEntity;
import com.ourhour.domain.project.mapper.MilestoneMapper;
import com.ourhour.domain.project.repository.MilestoneRepository;
import com.ourhour.domain.project.repository.ProjectRepository;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.exception.BusinessException;

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
            throw BusinessException.badRequest("유효하지 않은 프로젝트 ID입니다.");
        }

        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> BusinessException.badRequest("존재하지 않는 프로젝트 ID입니다."));

        if (projectEntity == null) {
            throw BusinessException.badRequest("프로젝트 정보를 찾을 수 없습니다.");
        }

        MilestoneEntity milestoneEntity = milestoneMapper.toMilestoneEntity(projectEntity, milestoneReqDTO);

        milestoneRepository.save(milestoneEntity);

        return ApiResponse.success(null, "마일스톤 등록에 성공했습니다.");
    }

}
