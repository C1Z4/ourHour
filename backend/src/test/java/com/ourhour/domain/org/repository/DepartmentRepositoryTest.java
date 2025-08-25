package com.ourhour.domain.org.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ourhour.domain.org.entity.DepartmentEntity;
import com.ourhour.domain.org.entity.OrgEntity;

@ExtendWith(MockitoExtension.class)
@DisplayName("DepartmentRepository 테스트")
class DepartmentRepositoryTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Test
    @DisplayName("부서명으로 부서 조회")
    void findByName() {
        // given
        String departmentName = "개발팀";
        DepartmentEntity department = DepartmentEntity.builder()
                .name(departmentName)
                .build();

        given(departmentRepository.findByName(departmentName))
                .willReturn(Optional.of(department));

        // when
        Optional<DepartmentEntity> result = departmentRepository.findByName(departmentName);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo(departmentName);
    }

    @Test
    @DisplayName("존재하지 않는 부서명으로 조회 - 빈 결과")
    void findByName_NotFound() {
        // given
        String nonExistentName = "존재하지않는부서";

        given(departmentRepository.findByName(nonExistentName))
                .willReturn(Optional.empty());

        // when
        Optional<DepartmentEntity> result = departmentRepository.findByName(nonExistentName);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("조직별 부서 목록 조회")
    void findByOrgEntity() {
        // given
        OrgEntity org = OrgEntity.builder()
                .name("테스트 조직")
                .build();

        DepartmentEntity dept1 = DepartmentEntity.builder()
                .name("개발팀")
                .orgEntity(org)
                .build();

        DepartmentEntity dept2 = DepartmentEntity.builder()
                .name("디자인팀")
                .orgEntity(org)
                .build();

        DepartmentEntity dept3 = DepartmentEntity.builder()
                .name("기획팀")
                .orgEntity(org)
                .build();

        List<DepartmentEntity> departments = Arrays.asList(dept1, dept2, dept3);

        given(departmentRepository.findByOrgEntity(org))
                .willReturn(departments);

        // when
        List<DepartmentEntity> result = departmentRepository.findByOrgEntity(org);

        // then
        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting(DepartmentEntity::getName)
                .containsExactly("개발팀", "디자인팀", "기획팀");
        assertThat(result)
                .allSatisfy(dept -> assertThat(dept.getOrgEntity()).isEqualTo(org));
    }

    @Test
    @DisplayName("조직별 부서 목록 조회 - 빈 결과")
    void findByOrgEntity_EmptyResult() {
        // given
        OrgEntity org = OrgEntity.builder()
                .name("부서없는조직")
                .build();

        given(departmentRepository.findByOrgEntity(org))
                .willReturn(List.of());

        // when
        List<DepartmentEntity> result = departmentRepository.findByOrgEntity(org);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("부서명 존재 여부 확인")
    void existsByName() {
        // given
        String existingName = "개발팀";
        String nonExistingName = "존재하지않는부서";

        given(departmentRepository.existsByName(existingName))
                .willReturn(true);
        given(departmentRepository.existsByName(nonExistingName))
                .willReturn(false);

        // when & then
        assertThat(departmentRepository.existsByName(existingName)).isTrue();
        assertThat(departmentRepository.existsByName(nonExistingName)).isFalse();
    }

    @Test
    @DisplayName("조직 내 부서명 중복 확인")
    void existsByNameAndOrgEntity() {
        // given
        String departmentName = "개발팀";
        OrgEntity org1 = OrgEntity.builder()
                .name("첫 번째 조직")
                .build();

        OrgEntity org2 = OrgEntity.builder()
                .name("두 번째 조직")
                .build();

        given(departmentRepository.existsByNameAndOrgEntity(departmentName, org1))
                .willReturn(true);
        given(departmentRepository.existsByNameAndOrgEntity(departmentName, org2))
                .willReturn(false);

        // when & then
        assertThat(departmentRepository.existsByNameAndOrgEntity(departmentName, org1)).isTrue();
        assertThat(departmentRepository.existsByNameAndOrgEntity(departmentName, org2)).isFalse();
    }

    @Test
    @DisplayName("같은 부서명이 다른 조직에는 중복 가능")
    void existsByNameAndOrgEntity_DifferentOrgs() {
        // given
        String departmentName = "개발팀";
        OrgEntity org1 = OrgEntity.builder()
                .name("회사 A")
                .build();

        OrgEntity org2 = OrgEntity.builder()
                .name("회사 B")
                .build();

        given(departmentRepository.existsByNameAndOrgEntity(departmentName, org1))
                .willReturn(true);
        given(departmentRepository.existsByNameAndOrgEntity(departmentName, org2))
                .willReturn(true);

        // when & then
        assertThat(departmentRepository.existsByNameAndOrgEntity(departmentName, org1)).isTrue();
        assertThat(departmentRepository.existsByNameAndOrgEntity(departmentName, org2)).isTrue();
    }
}