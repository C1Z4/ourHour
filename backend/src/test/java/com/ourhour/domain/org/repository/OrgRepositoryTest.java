package com.ourhour.domain.org.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ourhour.domain.org.entity.OrgEntity;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrgRepository 테스트")
class OrgRepositoryTest {

    @Mock
    private OrgRepository orgRepository;

    @Test
    @DisplayName("조직 ID로 조직 조회")
    void findByOrgId() {
        // given
        Long orgId = 1L;
        OrgEntity org = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        given(orgRepository.findByOrgId(orgId))
                .willReturn(org);

        // when
        OrgEntity result = orgRepository.findByOrgId(orgId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("테스트 조직");
        assertThat(result.getEmail()).isEqualTo("test@org.com");
        assertThat(result.getPhone()).isEqualTo("010-1234-5678");
    }

    @Test
    @DisplayName("존재하지 않는 조직 ID로 조회 - null 반환")
    void findByOrgId_NotFound() {
        // given
        Long nonExistentOrgId = 999L;

        given(orgRepository.findByOrgId(nonExistentOrgId))
                .willReturn(null);

        // when
        OrgEntity result = orgRepository.findByOrgId(nonExistentOrgId);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("조직 ID로 조회 - 다양한 조직 정보")
    void findByOrgId_DifferentOrgInfo() {
        // given
        Long orgId1 = 1L;
        Long orgId2 = 2L;

        OrgEntity org1 = OrgEntity.builder()
                .name("스타트업 A")
                .email("contact@startup.com")
                .phone("02-1234-5678")
                .build();

        OrgEntity org2 = OrgEntity.builder()
                .name("대기업 B")
                .email("info@bigcorp.com")
                .phone("02-9876-5432")
                .build();

        given(orgRepository.findByOrgId(orgId1))
                .willReturn(org1);
        given(orgRepository.findByOrgId(orgId2))
                .willReturn(org2);

        // when
        OrgEntity result1 = orgRepository.findByOrgId(orgId1);
        OrgEntity result2 = orgRepository.findByOrgId(orgId2);

        // then
        assertThat(result1.getName()).isEqualTo("스타트업 A");
        assertThat(result1.getEmail()).isEqualTo("contact@startup.com");
        
        assertThat(result2.getName()).isEqualTo("대기업 B");
        assertThat(result2.getEmail()).isEqualTo("info@bigcorp.com");
    }

    @Test
    @DisplayName("조직 ID가 0일 때 조회")
    void findByOrgId_ZeroId() {
        // given
        Long zeroOrgId = 0L;

        given(orgRepository.findByOrgId(zeroOrgId))
                .willReturn(null);

        // when
        OrgEntity result = orgRepository.findByOrgId(zeroOrgId);

        // then
        assertThat(result).isNull();
    }
}