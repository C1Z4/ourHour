package com.ourhour.domain.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import com.ourhour.domain.org.exception.OrgException;
import com.ourhour.domain.org.repository.OrgRepository;
import com.ourhour.domain.project.dto.ProjectParticipantDTO;
import com.ourhour.domain.project.entity.ProjectParticipantEntity;
import com.ourhour.domain.project.entity.ProjectParticipantId;
import com.ourhour.domain.project.exception.ProjectException;
import com.ourhour.domain.project.mapper.ProjectParticipantMapper;
import com.ourhour.domain.project.repository.ProjectParticipantRepository;
import com.ourhour.domain.project.repository.ProjectRepository;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.common.dto.PageResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectParticipantService 테스트")
class ProjectParticipantServiceTest {

    @Mock
    private ProjectParticipantRepository projectParticipantRepository;

    @Mock
    private ProjectParticipantMapper projectParticipantMapper;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private OrgRepository orgRepository;

    @InjectMocks
    private ProjectParticipantService projectParticipantService;

    private ProjectParticipantEntity participantEntity;
    private ProjectParticipantDTO participantDTO;
    private ProjectParticipantId participantId;

    @BeforeEach
    void setUp() {
        participantEntity = mock(ProjectParticipantEntity.class);
        participantDTO = mock(ProjectParticipantDTO.class);
        participantId = new ProjectParticipantId(1L, 1L);
    }

    @Test
    @DisplayName("프로젝트 참가자 목록 조회 성공 - 검색어 없음")
    void getProjectParticipants_Success_NoSearch() {
        // given
        Long projectId = 1L;
        Long orgId = 1L;
        String search = null;
        Pageable pageable = PageRequest.of(0, 10);

        given(projectRepository.existsById(projectId)).willReturn(true);
        given(orgRepository.existsById(orgId)).willReturn(true);

        Page<ProjectParticipantEntity> participantPage = new PageImpl<>(List.of(participantEntity), pageable, 1);
        given(projectParticipantRepository.findByProjectParticipantId_ProjectId(projectId, pageable))
                .willReturn(participantPage);

        given(projectParticipantMapper.toProjectParticipantDTO(participantEntity, orgId))
                .willReturn(participantDTO);

        // when
        ApiResponse<PageResponse<ProjectParticipantDTO>> result = projectParticipantService
                .getProjectParticipants(projectId, orgId, search, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        then(projectRepository).should().existsById(projectId);
        then(orgRepository).should().existsById(orgId);
        then(projectParticipantRepository).should().findByProjectParticipantId_ProjectId(projectId, pageable);
    }

    @Test
    @DisplayName("프로젝트 참가자 목록 조회 성공 - 검색어 있음")
    void getProjectParticipants_Success_WithSearch() {
        // given
        Long projectId = 1L;
        Long orgId = 1L;
        String search = "테스트";
        Pageable pageable = PageRequest.of(0, 10);

        given(projectRepository.existsById(projectId)).willReturn(true);
        given(orgRepository.existsById(orgId)).willReturn(true);

        Page<ProjectParticipantEntity> participantPage = new PageImpl<>(List.of(participantEntity), pageable, 1);
        given(projectParticipantRepository.findByProjectParticipantId_ProjectIdAndMemberNameContaining(projectId,
                search.trim(), pageable))
                .willReturn(participantPage);

        given(projectParticipantMapper.toProjectParticipantDTO(participantEntity, orgId))
                .willReturn(participantDTO);

        // when
        ApiResponse<PageResponse<ProjectParticipantDTO>> result = projectParticipantService
                .getProjectParticipants(projectId, orgId, search, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        then(projectParticipantRepository).should()
                .findByProjectParticipantId_ProjectIdAndMemberNameContaining(projectId, search.trim(), pageable);
    }

    @Test
    @DisplayName("프로젝트 참가자 목록 조회 시 잘못된 프로젝트 ID로 예외 발생")
    void getProjectParticipants_InvalidProjectId_ThrowsException() {
        // given
        Long invalidProjectId = 0L;
        Long orgId = 1L;
        String search = null;
        Pageable pageable = PageRequest.of(0, 10);

        // when & then
        assertThatThrownBy(
                () -> projectParticipantService.getProjectParticipants(invalidProjectId, orgId, search, pageable))
                .isInstanceOf(ProjectException.class);
    }

    @Test
    @DisplayName("프로젝트 참가자 목록 조회 시 존재하지 않는 프로젝트로 예외 발생")
    void getProjectParticipants_ProjectNotFound_ThrowsException() {
        // given
        Long projectId = 999L;
        Long orgId = 1L;
        String search = null;
        Pageable pageable = PageRequest.of(0, 10);

        given(projectRepository.existsById(projectId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> projectParticipantService.getProjectParticipants(projectId, orgId, search, pageable))
                .isInstanceOf(ProjectException.class);
    }

    @Test
    @DisplayName("프로젝트 참가자 목록 조회 시 존재하지 않는 조직으로 예외 발생")
    void getProjectParticipants_OrgNotFound_ThrowsException() {
        // given
        Long projectId = 1L;
        Long orgId = 999L;
        String search = null;
        Pageable pageable = PageRequest.of(0, 10);

        given(projectRepository.existsById(projectId)).willReturn(true);
        given(orgRepository.existsById(orgId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> projectParticipantService.getProjectParticipants(projectId, orgId, search, pageable))
                .isInstanceOf(OrgException.class);
    }

    @Test
    @DisplayName("프로젝트 참가자 목록 조회 시 빈 결과 반환")
    void getProjectParticipants_EmptyResult_Success() {
        // given
        Long projectId = 1L;
        Long orgId = 1L;
        String search = null;
        Pageable pageable = PageRequest.of(0, 10);

        given(projectRepository.existsById(projectId)).willReturn(true);
        given(orgRepository.existsById(orgId)).willReturn(true);

        Page<ProjectParticipantEntity> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        given(projectParticipantRepository.findByProjectParticipantId_ProjectId(projectId, pageable))
                .willReturn(emptyPage);

        // when
        ApiResponse<PageResponse<ProjectParticipantDTO>> result = projectParticipantService
                .getProjectParticipants(projectId, orgId, search, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("프로젝트 참가자 여부 확인 성공")
    void isProjectParticipant_Success() {
        // given
        Long projectId = 1L;
        Long memberId = 1L;

        given(projectParticipantRepository.existsById(participantId)).willReturn(true);

        // when
        boolean result = projectParticipantService.isProjectParticipant(projectId, memberId);

        // then
        assertThat(result).isTrue();
        then(projectParticipantRepository).should().existsById(participantId);
    }

    @Test
    @DisplayName("프로젝트 참가자 삭제 성공")
    void deleteProjectParticipant_Success() {
        // given
        Long projectId = 1L;
        Long memberId = 1L;

        given(projectRepository.existsById(projectId)).willReturn(true);

        // when
        ApiResponse<Void> result = projectParticipantService.deleteProjectParticipant(projectId, memberId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        then(projectRepository).should().existsById(projectId);
        then(projectParticipantRepository).should().deleteById(participantId);
    }

    @Test
    @DisplayName("프로젝트 참가자 삭제 시 잘못된 프로젝트 ID로 예외 발생")
    void deleteProjectParticipant_InvalidProjectId_ThrowsException() {
        // given
        Long invalidProjectId = 0L;
        Long memberId = 1L;

        // when & then
        assertThatThrownBy(() -> projectParticipantService.deleteProjectParticipant(invalidProjectId, memberId))
                .isInstanceOf(ProjectException.class);
    }

    @Test
    @DisplayName("프로젝트 참가자 삭제 시 잘못된 멤버 ID로 예외 발생")
    void deleteProjectParticipant_InvalidMemberId_ThrowsException() {
        // given
        Long projectId = 1L;
        Long invalidMemberId = 0L;

        // when & then
        assertThatThrownBy(() -> projectParticipantService.deleteProjectParticipant(projectId, invalidMemberId))
                .isInstanceOf(ProjectException.class);
    }

    @Test
    @DisplayName("프로젝트 참가자 삭제 시 존재하지 않는 프로젝트로 예외 발생")
    void deleteProjectParticipant_ProjectNotFound_ThrowsException() {
        // given
        Long projectId = 999L;
        Long memberId = 1L;

        given(projectRepository.existsById(projectId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> projectParticipantService.deleteProjectParticipant(projectId, memberId))
                .isInstanceOf(ProjectException.class);
    }
}