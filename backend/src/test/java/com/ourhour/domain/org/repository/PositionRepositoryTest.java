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

import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.entity.PositionEntity;

@ExtendWith(MockitoExtension.class)
@DisplayName("PositionRepository 테스트")
class PositionRepositoryTest {

    @Mock
    private PositionRepository positionRepository;

    @Test
    @DisplayName("직책명으로 직책 조회")
    void findByName() {
        // given
        String positionName = "팀장";
        PositionEntity position = PositionEntity.builder()
                .name(positionName)
                .build();

        given(positionRepository.findByName(positionName))
                .willReturn(Optional.of(position));

        // when
        Optional<PositionEntity> result = positionRepository.findByName(positionName);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo(positionName);
    }

    @Test
    @DisplayName("존재하지 않는 직책명으로 조회 - 빈 결과")
    void findByName_NotFound() {
        // given
        String nonExistentName = "존재하지않는직책";

        given(positionRepository.findByName(nonExistentName))
                .willReturn(Optional.empty());

        // when
        Optional<PositionEntity> result = positionRepository.findByName(nonExistentName);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("조직별 직책 목록 조회")
    void findByOrgEntity() {
        // given
        OrgEntity org = OrgEntity.builder()
                .name("테스트 조직")
                .build();

        PositionEntity position1 = PositionEntity.builder()
                .name("팀장")
                .orgEntity(org)
                .build();

        PositionEntity position2 = PositionEntity.builder()
                .name("선임")
                .orgEntity(org)
                .build();

        PositionEntity position3 = PositionEntity.builder()
                .name("주임")
                .orgEntity(org)
                .build();

        List<PositionEntity> positions = Arrays.asList(position1, position2, position3);

        given(positionRepository.findByOrgEntity(org))
                .willReturn(positions);

        // when
        List<PositionEntity> result = positionRepository.findByOrgEntity(org);

        // then
        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting(PositionEntity::getName)
                .containsExactly("팀장", "선임", "주임");
        assertThat(result)
                .allSatisfy(position -> assertThat(position.getOrgEntity()).isEqualTo(org));
    }

    @Test
    @DisplayName("조직별 직책 목록 조회 - 빈 결과")
    void findByOrgEntity_EmptyResult() {
        // given
        OrgEntity org = OrgEntity.builder()
                .name("직책없는조직")
                .build();

        given(positionRepository.findByOrgEntity(org))
                .willReturn(List.of());

        // when
        List<PositionEntity> result = positionRepository.findByOrgEntity(org);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("직책명 존재 여부 확인")
    void existsByName() {
        // given
        String existingName = "팀장";
        String nonExistingName = "존재하지않는직책";

        given(positionRepository.existsByName(existingName))
                .willReturn(true);
        given(positionRepository.existsByName(nonExistingName))
                .willReturn(false);

        // when & then
        assertThat(positionRepository.existsByName(existingName)).isTrue();
        assertThat(positionRepository.existsByName(nonExistingName)).isFalse();
    }

    @Test
    @DisplayName("조직 내 직책명 중복 확인")
    void existsByNameAndOrgEntity() {
        // given
        String positionName = "팀장";
        OrgEntity org1 = OrgEntity.builder()
                .name("첫 번째 조직")
                .build();

        OrgEntity org2 = OrgEntity.builder()
                .name("두 번째 조직")
                .build();

        given(positionRepository.existsByNameAndOrgEntity(positionName, org1))
                .willReturn(true);
        given(positionRepository.existsByNameAndOrgEntity(positionName, org2))
                .willReturn(false);

        // when & then
        assertThat(positionRepository.existsByNameAndOrgEntity(positionName, org1)).isTrue();
        assertThat(positionRepository.existsByNameAndOrgEntity(positionName, org2)).isFalse();
    }

    @Test
    @DisplayName("같은 직책명이 다른 조직에는 중복 가능")
    void existsByNameAndOrgEntity_DifferentOrgs() {
        // given
        String positionName = "팀장";
        OrgEntity org1 = OrgEntity.builder()
                .name("회사 A")
                .build();

        OrgEntity org2 = OrgEntity.builder()
                .name("회사 B")
                .build();

        given(positionRepository.existsByNameAndOrgEntity(positionName, org1))
                .willReturn(true);
        given(positionRepository.existsByNameAndOrgEntity(positionName, org2))
                .willReturn(true);

        // when & then
        assertThat(positionRepository.existsByNameAndOrgEntity(positionName, org1)).isTrue();
        assertThat(positionRepository.existsByNameAndOrgEntity(positionName, org2)).isTrue();
    }
}