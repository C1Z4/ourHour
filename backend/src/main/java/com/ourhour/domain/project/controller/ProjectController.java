package com.ourhour.domain.project.controller;

import com.ourhour.domain.project.dto.MileStoneInfoDTO;
import com.ourhour.domain.project.dto.ProjectInfoDTO;
import com.ourhour.domain.project.dto.ProjectParticipantDTO;
import com.ourhour.domain.project.dto.ProjectSummaryResDTO;
import com.ourhour.domain.project.service.ProjectParticipantService;
import com.ourhour.domain.project.service.ProjectService;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.common.dto.PageResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Validated
public class ProjectController {

        private final ProjectService projectService;
        private final ProjectParticipantService projectParticipantService;

        // 프로젝트 요약 목록 조회
        @GetMapping("/{orgId}")
        public ResponseEntity<ApiResponse<PageResponse<ProjectSummaryResDTO>>> getProjectsSummary(
                        @PathVariable @Min(value = 1, message = "조직 ID는 1 이상이어야 합니다.") Long orgId,
                        @RequestParam(defaultValue = "3") @Min(value = 1, message = "참여자 제한은 1 이상이어야 합니다.") @Max(value = 10, message = "참여자 제한은 10 이하여야 합니다.") int participantLimit,
                        @RequestParam(defaultValue = "0") @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.") int currentPage,
                        @RequestParam(defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.") @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.") int size) {

                Pageable pageable = PageRequest.of(currentPage, size, Sort.by(Sort.Direction.ASC, "projectId"));

                ApiResponse<PageResponse<ProjectSummaryResDTO>> response = projectService.getProjectsSummaryList(orgId,
                                participantLimit, pageable);

                return ResponseEntity.ok(response);
        }

        // 프로젝트 참여자 목록 조회
        @GetMapping("/{projectId}/participants")
        public ResponseEntity<ApiResponse<PageResponse<ProjectParticipantDTO>>> getProjectParticipants(
                        @PathVariable @Min(value = 1, message = "프로젝트 ID는 1 이상이어야 합니다.") Long projectId,
                        @RequestParam(defaultValue = "0") @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다 .") int currentPage,
                        @RequestParam(defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.") @Max(value = 100, message = " 페이지 크기는 100이하여야 합니다.") int size) {

                Pageable pageable = PageRequest.of(currentPage, size,
                                Sort.by(Sort.Direction.ASC, "ProjectParticipantId.memberId"));

                ApiResponse<PageResponse<ProjectParticipantDTO>> response = projectParticipantService
                                .getProjectParticipants(projectId, pageable);

                return ResponseEntity.ok(response);
        }

        // 프로젝트 정보 조회
        @GetMapping("/{projectId}/info")
        public ResponseEntity<ApiResponse<ProjectInfoDTO>> getProjectInfo(
                        @PathVariable @Min(value = 1, message = "프로젝트 ID는 1 이상이어야 합니다.") Long projectId) {
                ApiResponse<ProjectInfoDTO> response = projectService.getProjectInfo(projectId);

                return ResponseEntity.ok(response);
        }

        // 특정 프로젝트의 마일스톤 목록 조회
        @GetMapping("/{projectId}/milestones")
        public ResponseEntity<ApiResponse<PageResponse<MileStoneInfoDTO>>> getProjectMilestones(
                        @PathVariable @Min(value = 1, message = "프로젝트 ID는 1 이상이어야 합니다.") Long projectId,
                        @RequestParam(defaultValue = "0") @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.") int currentPage,
                        @RequestParam(defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.") @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.") int size) {
                Pageable pageable = PageRequest.of(currentPage, size, Sort.by(Sort.Direction.ASC, "milestoneId"));
                ApiResponse<PageResponse<MileStoneInfoDTO>> response = projectService.getProjectMilestones(projectId, pageable);
                return ResponseEntity.ok(response);
        }


}