package com.ourhour.domain.project.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.project.entity.ProjectEntity;
import com.ourhour.domain.project.enums.ProjectStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectRepository 테스트")
class ProjectRepositoryTest {

        @Mock
        private ProjectRepository projectRepository;

        @Test
        @DisplayName("조직 ID로 프로젝트 목록 페이징 조회")
        void findByOrgEntity_OrgId() {
                // given
                Long orgId = 1L;
                Pageable pageable = PageRequest.of(0, 10);

                OrgEntity org = OrgEntity.builder()
                                .name("테스트 조직")
                                .build();

                ProjectEntity project1 = ProjectEntity.builder()
                                .name("프로젝트 1")
                                .description("첫 번째 프로젝트")
                                .status(ProjectStatus.IN_PROGRESS)
                                .orgEntity(org)
                                .build();

                ProjectEntity project2 = ProjectEntity.builder()
                                .name("프로젝트 2")
                                .description("두 번째 프로젝트")
                                .status(ProjectStatus.IN_PROGRESS)
                                .orgEntity(org)
                                .build();

                ProjectEntity project3 = ProjectEntity.builder()
                                .name("프로젝트 3")
                                .description("세 번째 프로젝트")
                                .status(ProjectStatus.DONE)
                                .orgEntity(org)
                                .build();

                List<ProjectEntity> projects = Arrays.asList(project1, project2, project3);
                Page<ProjectEntity> projectPage = new PageImpl<>(projects, pageable, projects.size());

                given(projectRepository.findByOrgEntity_OrgId(orgId, pageable))
                                .willReturn(projectPage);

                // when
                Page<ProjectEntity> result = projectRepository.findByOrgEntity_OrgId(orgId, pageable);

                // then
                assertThat(result.getContent()).hasSize(3);
                assertThat(result.getContent())
                                .extracting(ProjectEntity::getName)
                                .containsExactly("프로젝트 1", "프로젝트 2", "프로젝트 3");
                assertThat(result.getContent())
                                .allSatisfy(project -> assertThat(project.getOrgEntity()).isNotNull());
        }

        @Test
        @DisplayName("조직 ID로 프로젝트 목록 조회 - 빈 결과")
        void findByOrgEntity_OrgId_EmptyResult() {
                // given
                Long orgId = 999L;
                Pageable pageable = PageRequest.of(0, 10);

                Page<ProjectEntity> emptyPage = new PageImpl<>(List.of(), pageable, 0);

                given(projectRepository.findByOrgEntity_OrgId(orgId, pageable))
                                .willReturn(emptyPage);

                // when
                Page<ProjectEntity> result = projectRepository.findByOrgEntity_OrgId(orgId, pageable);

                // then
                assertThat(result.getContent()).isEmpty();
                assertThat(result.getTotalElements()).isEqualTo(0);
                assertThat(result.hasContent()).isFalse();
        }

        @Test
        @DisplayName("프로젝트 ID로 조직 ID 조회")
        void findOrgIdByProjectId() {
                // given
                Long projectId = 1L;
                Long expectedOrgId = 1L;

                given(projectRepository.findOrgIdByProjectId(projectId))
                                .willReturn(expectedOrgId);

                // when
                Long result = projectRepository.findOrgIdByProjectId(projectId);

                // then
                assertThat(result).isEqualTo(expectedOrgId);
        }

        @Test
        @DisplayName("존재하지 않는 프로젝트 ID로 조직 ID 조회 - null 반환")
        void findOrgIdByProjectId_NotFound() {
                // given
                Long nonExistentProjectId = 999L;

                given(projectRepository.findOrgIdByProjectId(nonExistentProjectId))
                                .willReturn(null);

                // when
                Long result = projectRepository.findOrgIdByProjectId(nonExistentProjectId);

                // then
                assertThat(result).isNull();
        }

        @Test
        @DisplayName("다양한 상태의 프로젝트가 모두 조회되는지 확인")
        void findByOrgEntity_OrgId_DifferentStatuses() {
                // given
                Long orgId = 1L;
                Pageable pageable = PageRequest.of(0, 5);

                OrgEntity org = OrgEntity.builder()
                                .name("테스트 조직")
                                .build();

                ProjectEntity activeProject = ProjectEntity.builder()
                                .name("활성 프로젝트")
                                .status(ProjectStatus.IN_PROGRESS)
                                .orgEntity(org)
                                .build();

                ProjectEntity completedProject = ProjectEntity.builder()
                                .name("완료된 프로젝트")
                                .status(ProjectStatus.DONE)
                                .orgEntity(org)
                                .build();

                List<ProjectEntity> projects = Arrays.asList(activeProject, completedProject);
                Page<ProjectEntity> projectPage = new PageImpl<>(projects, pageable, projects.size());

                given(projectRepository.findByOrgEntity_OrgId(orgId, pageable))
                                .willReturn(projectPage);

                // when
                Page<ProjectEntity> result = projectRepository.findByOrgEntity_OrgId(orgId, pageable);

                // then
                assertThat(result.getContent()).hasSize(2);
                assertThat(result.getContent())
                                .extracting(ProjectEntity::getStatus)
                                .containsExactlyInAnyOrder(ProjectStatus.IN_PROGRESS, ProjectStatus.DONE);
                assertThat(result.getContent())
                                .extracting(ProjectEntity::getName)
                                .containsExactlyInAnyOrder("활성 프로젝트", "완료된 프로젝트");
        }

        @Test
        @DisplayName("페이징 정보가 올바르게 처리되는지 확인")
        void findByOrgEntity_OrgId_PagingInfo() {
                // given
                Long orgId = 1L;
                Pageable pageable = PageRequest.of(1, 2); // 두 번째 페이지, 페이지당 2개

                OrgEntity org = OrgEntity.builder()
                                .name("테스트 조직")
                                .build();

                ProjectEntity project3 = ProjectEntity.builder()
                                .name("프로젝트 3")
                                .description("세 번째 프로젝트")
                                .orgEntity(org)
                                .build();

                ProjectEntity project4 = ProjectEntity.builder()
                                .name("프로젝트 4")
                                .description("네 번째 프로젝트")
                                .orgEntity(org)
                                .build();

                List<ProjectEntity> projects = Arrays.asList(project3, project4);
                Page<ProjectEntity> projectPage = new PageImpl<>(projects, pageable, 5); // 전체 5개 중 2개

                given(projectRepository.findByOrgEntity_OrgId(orgId, pageable))
                                .willReturn(projectPage);

                // when
                Page<ProjectEntity> result = projectRepository.findByOrgEntity_OrgId(orgId, pageable);

                // then
                assertThat(result.getContent()).hasSize(2);
                assertThat(result.getNumber()).isEqualTo(1); // 현재 페이지 번호
                assertThat(result.getSize()).isEqualTo(2); // 페이지 크기
                assertThat(result.getTotalElements()).isEqualTo(5); // 전체 요소 수
                assertThat(result.getTotalPages()).isEqualTo(3); // 전체 페이지 수 (5/2 = 2.5 -> 3)
                assertThat(result.hasNext()).isTrue(); // 다음 페이지 존재
                assertThat(result.hasPrevious()).isTrue(); // 이전 페이지 존재
        }
}