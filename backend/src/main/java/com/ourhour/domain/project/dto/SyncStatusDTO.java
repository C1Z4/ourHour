package com.ourhour.domain.project.dto;

import java.time.LocalDateTime;

import com.ourhour.domain.project.enums.SyncStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncStatusDTO {
    private LocalDateTime lastSyncedAt;
    private SyncStatus syncStatus;
    private Long syncedIssues;
    private Long totalIssues;
    private Long syncedMilestones;
    private Long totalMilestones;
}
