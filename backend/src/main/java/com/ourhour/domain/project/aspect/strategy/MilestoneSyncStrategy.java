package com.ourhour.domain.project.aspect.strategy;

import com.ourhour.domain.project.dto.MileStoneInfoDTO;
import com.ourhour.domain.project.entity.GitHubSyncableEntity;
import com.ourhour.domain.project.repository.MilestoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MilestoneSyncStrategy implements EntitySyncStrategy {

    private final MilestoneRepository milestoneRepository;

    @Override
    public boolean supports(String entityType) {
        return "milestoneId".equalsIgnoreCase(entityType) || "milestone".equalsIgnoreCase(entityType);
    }

    @Override
    public GitHubSyncableEntity findById(Long id) {
        return milestoneRepository.findById(id).orElse(null);
    }

    @Override
    public GitHubSyncableEntity extractFromDto(Object dto) {
        if (dto instanceof MileStoneInfoDTO) {
            MileStoneInfoDTO milestoneDto = (MileStoneInfoDTO) dto;
            return milestoneRepository.findById(milestoneDto.getMilestoneId()).orElse(null);
        }
        return null;
    }
}
