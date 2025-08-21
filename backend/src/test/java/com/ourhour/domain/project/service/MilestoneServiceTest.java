package com.ourhour.domain.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.ourhour.domain.member.exception.MemberException;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.project.dto.MilestoneReqDTO;
import com.ourhour.domain.project.entity.MilestoneEntity;
import com.ourhour.domain.project.entity.ProjectEntity;
import com.ourhour.domain.project.exception.MilestoneException;
import com.ourhour.domain.project.exception.ProjectException;
import com.ourhour.domain.project.mapper.MilestoneMapper;
import com.ourhour.domain.project.repository.MilestoneRepository;
import com.ourhour.domain.project.repository.ProjectRepository;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.util.SecurityUtil;

@ExtendWith(MockitoExtension.class)
@DisplayName("MilestoneService 테스트")
class MilestoneServiceTest {

    @Mock
    private MilestoneRepository milestoneRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MilestoneMapper milestoneMapper;

    @InjectMocks
    private MilestoneService milestoneService;

    private MilestoneEntity milestone;
    private ProjectEntity project;
    private OrgEntity org;
    private MilestoneReqDTO milestoneReqDTO;

    @BeforeEach
    void setUp() {
        milestone = mock(MilestoneEntity.class);
        project = mock(ProjectEntity.class);
        org = mock(OrgEntity.class);
        milestoneReqDTO = new MilestoneReqDTO();
        milestoneReqDTO.setName("테스트 마일스톤");
    }

    @Test
    @DisplayName("마일스톤 등록 성공")
    void createMilestone_Success() {
        // given
        Long projectId = 1L;

        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));
        given(milestoneRepository.findByProjectEntity_ProjectIdAndName(projectId, milestoneReqDTO.getName()))
                .willReturn(Optional.empty());
        given(milestoneMapper.toMilestoneEntity(project, milestoneReqDTO)).willReturn(milestone);
        given(milestoneRepository.save(milestone)).willReturn(milestone);

        // when
        ApiResponse<Void> result = milestoneService.createMilestone(projectId, milestoneReqDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        then(projectRepository).should().findById(projectId);
        then(milestoneRepository).should().findByProjectEntity_ProjectIdAndName(projectId, milestoneReqDTO.getName());
        then(milestoneMapper).should().toMilestoneEntity(project, milestoneReqDTO);
        then(milestoneRepository).should().save(milestone);
    }

    @Test
    @DisplayName("마일스톤 등록 시 잘못된 프로젝트 ID로 예외 발생")
    void createMilestone_InvalidProjectId_ThrowsException() {
        // given
        Long invalidProjectId = 0L;

        // when & then
        assertThatThrownBy(() -> milestoneService.createMilestone(invalidProjectId, milestoneReqDTO))
                .isInstanceOf(ProjectException.class);
    }

    @Test
    @DisplayName("마일스톤 등록 시 존재하지 않는 프로젝트로 예외 발생")
    void createMilestone_ProjectNotFound_ThrowsException() {
        // given
        Long projectId = 999L;

        given(projectRepository.findById(projectId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> milestoneService.createMilestone(projectId, milestoneReqDTO))
                .isInstanceOf(ProjectException.class);
    }

    @Test
    @DisplayName("마일스톤 등록 시 중복된 이름으로 예외 발생")
    void createMilestone_DuplicateName_ThrowsException() {
        // given
        Long projectId = 1L;

        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));
        given(milestoneRepository.findByProjectEntity_ProjectIdAndName(projectId, milestoneReqDTO.getName()))
                .willReturn(Optional.of(milestone));

        // when & then
        assertThatThrownBy(() -> milestoneService.createMilestone(projectId, milestoneReqDTO))
                .isInstanceOf(MilestoneException.class);
    }

    @Test
    @DisplayName("마일스톤 수정 성공")
    void updateMilestone_Success() {
        // given
        Long milestoneId = 1L;

        given(milestoneRepository.findById(milestoneId)).willReturn(Optional.of(milestone));
        given(milestoneRepository.save(milestone)).willReturn(milestone);

        // when
        ApiResponse<Void> result = milestoneService.updateMilestone(milestoneId, milestoneReqDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        then(milestoneRepository).should().findById(milestoneId);
        then(milestoneMapper).should().updateMilestoneEntity(milestone, milestoneReqDTO);
        then(milestoneRepository).should().save(milestone);
    }

    @Test
    @DisplayName("마일스톤 수정 시 잘못된 마일스톤 ID로 예외 발생")
    void updateMilestone_InvalidMilestoneId_ThrowsException() {
        // given
        Long invalidMilestoneId = 0L;

        // when & then
        assertThatThrownBy(() -> milestoneService.updateMilestone(invalidMilestoneId, milestoneReqDTO))
                .isInstanceOf(MilestoneException.class);
    }

    @Test
    @DisplayName("마일스톤 수정 시 존재하지 않는 마일스톤으로 예외 발생")
    void updateMilestone_MilestoneNotFound_ThrowsException() {
        // given
        Long milestoneId = 999L;

        given(milestoneRepository.findById(milestoneId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> milestoneService.updateMilestone(milestoneId, milestoneReqDTO))
                .isInstanceOf(MilestoneException.class);
    }

    @Test
    @DisplayName("마일스톤 삭제 성공")
    void deleteMilestone_Success() {
        // given
        Long milestoneId = 1L;
        Long orgId = 1L;
        Long memberId = 1L;

        given(milestoneRepository.findById(milestoneId)).willReturn(Optional.of(milestone));
        given(milestone.getProjectEntity()).willReturn(project);
        given(project.getOrgEntity()).willReturn(org);
        given(org.getOrgId()).willReturn(orgId);

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(() -> SecurityUtil.getCurrentMemberIdByOrgId(orgId))
                    .thenReturn(memberId);
            mockedSecurityUtil.when(() -> SecurityUtil.getCurrentRoleByOrgId(orgId))
                    .thenReturn(Role.ADMIN);

            // when
            ApiResponse<Void> result = milestoneService.deleteMilestone(milestoneId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
            then(milestoneRepository).should().findById(milestoneId);
            then(milestoneRepository).should().delete(milestone);
        }
    }

    @Test
    @DisplayName("마일스톤 삭제 시 잘못된 마일스톤 ID로 예외 발생")
    void deleteMilestone_InvalidMilestoneId_ThrowsException() {
        // given
        Long invalidMilestoneId = 0L;

        // when & then
        assertThatThrownBy(() -> milestoneService.deleteMilestone(invalidMilestoneId))
                .isInstanceOf(MilestoneException.class);
    }

    @Test
    @DisplayName("마일스톤 삭제 시 존재하지 않는 마일스톤으로 예외 발생")
    void deleteMilestone_MilestoneNotFound_ThrowsException() {
        // given
        Long milestoneId = 999L;

        given(milestoneRepository.findById(milestoneId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> milestoneService.deleteMilestone(milestoneId))
                .isInstanceOf(MilestoneException.class);
    }

    @Test
    @DisplayName("마일스톤 삭제 시 권한 없는 사용자로 예외 발생")
    void deleteMilestone_NoPermission_ThrowsException() {
        // given
        Long milestoneId = 1L;
        Long orgId = 1L;

        given(milestoneRepository.findById(milestoneId)).willReturn(Optional.of(milestone));
        given(milestone.getProjectEntity()).willReturn(project);
        given(project.getOrgEntity()).willReturn(org);
        given(org.getOrgId()).willReturn(orgId);

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(() -> SecurityUtil.getCurrentMemberIdByOrgId(orgId))
                    .thenReturn(null);

            // when & then
            assertThatThrownBy(() -> milestoneService.deleteMilestone(milestoneId))
                    .isInstanceOf(MemberException.class);
        }
    }

    @Test
    @DisplayName("마일스톤 삭제 시 일반 사용자 권한으로 예외 발생")
    void deleteMilestone_RegularUserPermission_ThrowsException() {
        // given
        Long milestoneId = 1L;
        Long orgId = 1L;
        Long memberId = 1L;

        given(milestoneRepository.findById(milestoneId)).willReturn(Optional.of(milestone));
        given(milestone.getProjectEntity()).willReturn(project);
        given(project.getOrgEntity()).willReturn(org);
        given(org.getOrgId()).willReturn(orgId);

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(() -> SecurityUtil.getCurrentMemberIdByOrgId(orgId))
                    .thenReturn(memberId);
            mockedSecurityUtil.when(() -> SecurityUtil.getCurrentRoleByOrgId(orgId))
                    .thenReturn(Role.MEMBER);

            // when & then
            assertThatThrownBy(() -> milestoneService.deleteMilestone(milestoneId))
                    .isInstanceOf(ProjectException.class);
        }
    }
}