package com.ourhour.domain.project.aspect.strategy;

import com.ourhour.domain.project.dto.IssueDetailDTO;
import com.ourhour.domain.project.entity.GitHubSyncableEntity;
import com.ourhour.domain.project.repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IssueSyncStrategy implements EntitySyncStrategy {

    private final IssueRepository issueRepository;

    @Override
    public boolean supports(String entityType) {
        return "issueId".equalsIgnoreCase(entityType) || "issue".equalsIgnoreCase(entityType);
    }

    @Override
    public GitHubSyncableEntity findById(Long id) {
        return issueRepository.findById(id).orElse(null);
    }

    @Override
    public GitHubSyncableEntity extractFromDto(Object dto) {
        if (dto instanceof IssueDetailDTO) {
            IssueDetailDTO issueDto = (IssueDetailDTO) dto;
            return issueRepository.findById(issueDto.getIssueId()).orElse(null);
        }
        return null;
    }
}
