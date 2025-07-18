package com.ourhour.domain.project.controller;

import com.ourhour.domain.project.dto.IssueSummaryDTO;
import com.ourhour.domain.project.dto.MilestoneReqDTO;
import com.ourhour.domain.project.dto.IssueDetailDTO;
import com.ourhour.domain.project.dto.IssueReqDTO;
import com.ourhour.domain.project.dto.ProjecUpdateReqDTO;
import com.ourhour.domain.project.dto.MileStoneInfoDTO;
import com.ourhour.domain.project.dto.ProjectInfoDTO;
import com.ourhour.domain.project.dto.ProjectParticipantDTO;
import com.ourhour.domain.project.dto.ProjectReqDTO;
import com.ourhour.domain.project.dto.ProjectSummaryResDTO;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.project.service.IssueService;
import com.ourhour.domain.project.service.MilestoneService;
import com.ourhour.domain.project.service.ProjectParticipantService;
import com.ourhour.domain.project.service.ProjectService;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.common.dto.PageResponse;
import com.ourhour.global.jwt.annotation.OrgAuth;
import com.ourhour.domain.project.annotation.ProjectParticipantOnly;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ourhour.global.jwt.util.UserContextHolder;
import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.exception.BusinessException;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Validated
public class ProjectController {

        private final ProjectService projectService;
        private final ProjectParticipantService projectParticipantService;
        private final IssueService issueService;
        private final MilestoneService milestoneService;

        // 프로젝트 등록
        @OrgAuth(accessLevel = Role.ADMIN)
        @PostMapping("/{orgId}")
        public ResponseEntity<ApiResponse<Void>> createProject(
                        @PathVariable @Min(value = 1, message = "조직 ID는 1 이상이어야 합니다.") Long orgId,
                        @Valid @RequestBody ProjectReqDTO projectReqDTO) {
                ApiResponse<Void> response = projectService.createProject(orgId, projectReqDTO);
                return ResponseEntity.ok(response);
        }

        // 프로젝트 수정(정보, 참가자)
        @OrgAuth(accessLevel = Role.ADMIN)
        @PutMapping("/{projectId}")
        public ResponseEntity<ApiResponse<Void>> updateProject(
                        @PathVariable @Min(value = 1, message = "프로젝트 ID는 1 이상이어야 합니다.") Long projectId,
                        @Valid @RequestBody ProjecUpdateReqDTO projectReqDTO) {
                ApiResponse<Void> response = projectService.updateProject(projectId, projectReqDTO);
                return ResponseEntity.ok(response);
        }

        // 프로젝트 삭제
        @OrgAuth(accessLevel = Role.ADMIN)
        @DeleteMapping("/{projectId}")
        public ResponseEntity<ApiResponse<Void>> deleteProject(
                        @PathVariable @Min(value = 1, message = "프로젝트 ID는 1 이상이어야 합니다.") Long projectId) {
                ApiResponse<Void> response = projectService.deleteProject(projectId);
                return ResponseEntity.ok(response);
        }

        // 프로젝트 요약 목록 조회
        @GetMapping("/{orgId}")
        public ResponseEntity<ApiResponse<PageResponse<ProjectSummaryResDTO>>> getProjectsSummary(
                        @PathVariable @Min(value = 1, message = "조직 ID는 1 이상이어야 합니다.") Long orgId,
                        @RequestParam(defaultValue = "3") @Min(value = 1, message = "참여자 제한은 1 이상이어야 합니다.") @Max(value = 10, message = "참여자 제한은 10 이하여야 합니다.") int participantLimit,
                        @RequestParam(defaultValue = "1") @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.") int currentPage,
                        @RequestParam(defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.") @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.") int size) {

                Pageable pageable = PageRequest.of(currentPage - 1, size, Sort.by(Sort.Direction.ASC, "projectId"));

                ApiResponse<PageResponse<ProjectSummaryResDTO>> response = projectService.getProjectsSummaryList(orgId,
                                participantLimit, pageable);

                return ResponseEntity.ok(response);
        }

        // 프로젝트 참여자 목록 조회
        @GetMapping("/{projectId}/{orgId}/participants")
        public ResponseEntity<ApiResponse<PageResponse<ProjectParticipantDTO>>> getProjectParticipants(
                        @PathVariable @Min(value = 1, message = "프로젝트 ID는 1 이상이어야 합니다.") Long projectId,
                        @PathVariable @Min(value = 1, message = "조직 ID는 1 이상이어야 합니다.") Long orgId,
                        @RequestParam(defaultValue = "1") @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다 .") int currentPage,
                        @RequestParam(defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.") @Max(value = 100, message = " 페이지 크기는 100이하여야 합니다.") int size) {

                Pageable pageable = PageRequest.of(currentPage - 1, size,
                                Sort.by(Sort.Direction.ASC, "ProjectParticipantId.memberId"));

                ApiResponse<PageResponse<ProjectParticipantDTO>> response = projectParticipantService
                                .getProjectParticipants(projectId, orgId, pageable);

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
                        @RequestParam(defaultValue = "1") @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.") int currentPage,
                        @RequestParam(defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.") @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.") int size) {

                Pageable pageable = PageRequest.of(currentPage - 1, size, Sort.by(Sort.Direction.ASC, "milestoneId"));

                ApiResponse<PageResponse<MileStoneInfoDTO>> response = projectService.getProjectMilestones(projectId,
                                pageable);

                return ResponseEntity.ok(response);
        }

        // 특정 마일스톤의 이슈 목록 조회 (milestoneId가 없으면 프로젝트의 모든 이슈 조회)
        @GetMapping("/{projectId}/issues")
        public ResponseEntity<ApiResponse<PageResponse<IssueSummaryDTO>>> getMilestoneIssues(
                        @PathVariable @Min(value = 1, message = "프로젝트 ID는 1 이상이어야 합니다.") Long projectId,
                        @RequestParam(required = false) Long milestoneId,
                        @RequestParam(defaultValue = "1") @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.") int currentPage,
                        @RequestParam(defaultValue = "10") @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.") @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.") int size) {

                Pageable pageable = PageRequest.of(currentPage - 1, size, Sort.by(Sort.Direction.ASC, "issueId"));

                ApiResponse<PageResponse<IssueSummaryDTO>> response = issueService.getMilestoneIssues(projectId,
                                milestoneId,
                                pageable);

                return ResponseEntity.ok(response);
        }

        // 마일스톤 등록
        @ProjectParticipantOnly
        @PostMapping("/{projectId}/milestones")
        public ResponseEntity<ApiResponse<Void>> createMilestone(
                        @PathVariable @Min(value = 1, message = "프로젝트 ID는 1 이상이어야 합니다.") Long projectId,
                        @Valid @RequestBody MilestoneReqDTO milestoneReqDTO) {

                ApiResponse<Void> response = milestoneService.createMilestone(projectId, milestoneReqDTO);

                return ResponseEntity.ok(response);
        }

        // 마일스톤 수정(마일스톤 이름)
        @ProjectParticipantOnly
        @PutMapping("/milestones/{milestoneId}")
        public ResponseEntity<ApiResponse<Void>> updateMilestone(
                        @PathVariable @Min(value = 1, message = "마일스톤 ID는 1 이상이어야 합니다.") Long milestoneId,
                        @Valid @RequestBody MilestoneReqDTO milestoneReqDTO) {

                ApiResponse<Void> response = milestoneService.updateMilestone(milestoneId, milestoneReqDTO);

                return ResponseEntity.ok(response);
        }

        // 마일스톤 삭제
        @DeleteMapping("/milestones/{milestoneId}")
        public ResponseEntity<ApiResponse<Void>> deleteMilestone(
                @PathVariable Long milestoneId) {

                Claims claims = UserContextHolder.get();
                if (claims == null) {
                    throw BusinessException.unauthorized("인증 정보가 없습니다.");
                }

                ApiResponse<Void> response = milestoneService.deleteMilestone(milestoneId, claims);
                return ResponseEntity.ok(response);
        }

        // 이슈 상세 조회
        @GetMapping("/issues/{issueId}")
        public ResponseEntity<ApiResponse<IssueDetailDTO>> getIssueInfo(
                        @PathVariable @Min(value = 1, message = "이슈 ID는 1 이상이어야 합니다.") Long issueId) {

                ApiResponse<IssueDetailDTO> response = issueService.getIssueDetail(issueId);

                return ResponseEntity.ok(response);
        }

        // 이슈 등록
        @ProjectParticipantOnly
        @PostMapping("/{projectId}/issues")
        public ResponseEntity<ApiResponse<IssueDetailDTO>> createIssue(
                        @PathVariable @Min(value = 1, message = "프로젝트 ID는 1 이상이어야 합니다.") Long projectId,
                        @Valid @RequestBody IssueReqDTO issueReqDTO) {

                ApiResponse<IssueDetailDTO> response = issueService.createIssue(projectId, issueReqDTO);

                return ResponseEntity.ok(response);
        }

        // 이슈 수정
        @ProjectParticipantOnly
        @PutMapping("/issues/{issueId}")
        public ResponseEntity<ApiResponse<IssueDetailDTO>> updateIssue(
                        @PathVariable @Min(value = 1, message = "이슈 ID는 1 이상이어야 합니다.") Long issueId,
                        @Valid @RequestBody IssueReqDTO issueReqDTO) {

                ApiResponse<IssueDetailDTO> response = issueService.updateIssue(issueId, issueReqDTO);

                return ResponseEntity.ok(response);
        }

        // 이슈 삭제
        @DeleteMapping("/issues/{issueId}")
        public ResponseEntity<ApiResponse<Void>> deleteIssue(
                        @PathVariable @Min(value = 1, message = "이슈 ID는 1 이상이어야 합니다.") Long issueId) {

                Claims claims = UserContextHolder.get();
                if (claims == null) {
                    throw BusinessException.unauthorized("인증 정보가 없습니다.");
                }

                ApiResponse<Void> response = issueService.deleteIssue(issueId, claims);

                return ResponseEntity.ok(response);
        }

        // 프로젝트 참여 여부 확인
        @GetMapping("/{projectId}/{memberId}/participation")
        public ResponseEntity<ApiResponse<Boolean>> checkProjectParticipant(
                        @PathVariable @Min(value = 1, message = "프로젝트 ID는 1 이상이어야 합니다.") Long projectId,
                        @PathVariable @Min(value = 1, message = "멤버 ID는 1 이상이어야 합니다.") Long memberId) {

                ApiResponse<Boolean> response = projectParticipantService.checkProjectParticipant(projectId, memberId);

                return ResponseEntity.ok(response);
        }

}