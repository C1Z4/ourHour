package com.ourhour.domain.org.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.org.entity.DepartmentEntity;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.org.entity.PositionEntity;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.org.enums.Status;
import com.ourhour.domain.user.entity.UserEntity;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrgParticipantMemberRepository 테스트")
class OrgParticipantMemberRepositoryTest {

    @Mock
    private OrgParticipantMemberRepository orgParticipantMemberRepository;

    @Test
    @DisplayName("조직 ID로 구성원 페이징 조회")
    void findByOrgId() {
        // given
        Long orgId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        OrgEntity org = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        UserEntity user1 = UserEntity.builder()
                .email("kim@test.com")
                .password("password")
                .build();

        UserEntity user2 = UserEntity.builder()
                .email("park@test.com")
                .password("password")
                .build();

        MemberEntity member1 = MemberEntity.builder()
                .userEntity(user1)
                .name("김개발")
                .email("kim@test.com")
                .build();

        MemberEntity member2 = MemberEntity.builder()
                .userEntity(user2)
                .name("박디자인")
                .email("park@test.com")
                .build();

        OrgParticipantMemberEntity participant1 = OrgParticipantMemberEntity.builder()
                .orgEntity(org)
                .memberEntity(member1)
                .role(Role.MEMBER)
                .status(Status.ACTIVE)
                .build();

        OrgParticipantMemberEntity participant2 = OrgParticipantMemberEntity.builder()
                .orgEntity(org)
                .memberEntity(member2)
                .role(Role.ADMIN)
                .status(Status.ACTIVE)
                .build();

        List<OrgParticipantMemberEntity> participants = Arrays.asList(participant1, participant2);
        Page<OrgParticipantMemberEntity> participantPage = new PageImpl<>(participants, pageable, participants.size());

        given(orgParticipantMemberRepository.findByOrgId(orgId, pageable))
                .willReturn(participantPage);

        // when
        Page<OrgParticipantMemberEntity> result = orgParticipantMemberRepository.findByOrgId(orgId, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(opm -> opm.getMemberEntity().getName())
                .containsExactly("김개발", "박디자인");
    }

    @Test
    @DisplayName("조직 ID와 이름 검색으로 구성원 페이징 조회")
    void findByOrgIdAndNameContaining() {
        // given
        Long orgId = 1L;
        String search = "김";
        Pageable pageable = PageRequest.of(0, 10);

        OrgEntity org = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        UserEntity user1 = UserEntity.builder()
                .email("kim@test.com")
                .password("password")
                .build();

        MemberEntity member1 = MemberEntity.builder()
                .userEntity(user1)
                .name("김개발")
                .email("kim@test.com")
                .build();

        OrgParticipantMemberEntity participant1 = OrgParticipantMemberEntity.builder()
                .orgEntity(org)
                .memberEntity(member1)
                .role(Role.MEMBER)
                .status(Status.ACTIVE)
                .build();

        List<OrgParticipantMemberEntity> participants = Arrays.asList(participant1);
        Page<OrgParticipantMemberEntity> participantPage = new PageImpl<>(participants, pageable, participants.size());

        given(orgParticipantMemberRepository.findByOrgIdAndNameContaining(orgId, search, pageable))
                .willReturn(participantPage);

        // when
        Page<OrgParticipantMemberEntity> result = orgParticipantMemberRepository.findByOrgIdAndNameContaining(orgId, search, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getMemberEntity().getName()).contains(search);
    }

    @Test
    @DisplayName("조직 및 구성원 존재 여부 확인")
    void existsByOrgEntity_OrgIdAndMemberEntity_MemberId() {
        // given
        Long orgId = 1L;
        Long memberId = 1L;

        given(orgParticipantMemberRepository.existsByOrgEntity_OrgIdAndMemberEntity_MemberId(orgId, memberId))
                .willReturn(true);
        given(orgParticipantMemberRepository.existsByOrgEntity_OrgIdAndMemberEntity_MemberId(orgId, 999L))
                .willReturn(false);

        // when & then
        assertThat(orgParticipantMemberRepository.existsByOrgEntity_OrgIdAndMemberEntity_MemberId(orgId, memberId)).isTrue();
        assertThat(orgParticipantMemberRepository.existsByOrgEntity_OrgIdAndMemberEntity_MemberId(orgId, 999L)).isFalse();
    }

    @Test
    @DisplayName("권한별 구성원 수 집계")
    void countByOrgEntity_OrgIdAndRole() {
        // given
        Long orgId = 1L;
        Role adminRole = Role.ADMIN;
        Role memberRole = Role.MEMBER;

        given(orgParticipantMemberRepository.countByOrgEntity_OrgIdAndRole(orgId, adminRole))
                .willReturn(2);
        given(orgParticipantMemberRepository.countByOrgEntity_OrgIdAndRole(orgId, memberRole))
                .willReturn(10);

        // when & then
        assertThat(orgParticipantMemberRepository.countByOrgEntity_OrgIdAndRole(orgId, adminRole)).isEqualTo(2);
        assertThat(orgParticipantMemberRepository.countByOrgEntity_OrgIdAndRole(orgId, memberRole)).isEqualTo(10);
    }

    @Test
    @DisplayName("루트 관리자 수 집계")
    void countByOrgEntity_OrgIdAndRole_ROOT_ADMIN() {
        // given
        Long orgId = 1L;
        Role rootAdminRole = Role.ROOT_ADMIN;

        given(orgParticipantMemberRepository.countByOrgEntity_OrgIdAndRole(orgId, rootAdminRole))
                .willReturn(1);

        // when
        int result = orgParticipantMemberRepository.countByOrgEntity_OrgIdAndRole(orgId, rootAdminRole);

        // then
        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("구성원 ID와 상태로 모든 조직 참여 정보 조회")
    void findAllByMemberEntity_MemberIdAndStatus() {
        // given
        Long memberId = 1L;
        Status status = Status.ACTIVE;

        UserEntity user = UserEntity.builder()
                .email("kim@test.com")
                .password("password")
                .build();

        MemberEntity member = MemberEntity.builder()
                .userEntity(user)
                .name("김개발")
                .email("kim@test.com")
                .build();

        OrgEntity org1 = OrgEntity.builder()
                .name("첫 번째 조직")
                .email("org1@test.com")
                .phone("010-1111-1111")
                .build();

        OrgEntity org2 = OrgEntity.builder()
                .name("두 번째 조직")
                .email("org2@test.com")
                .phone("010-2222-2222")
                .build();

        OrgParticipantMemberEntity participant1 = OrgParticipantMemberEntity.builder()
                .orgEntity(org1)
                .memberEntity(member)
                .role(Role.MEMBER)
                .status(status)
                .build();

        OrgParticipantMemberEntity participant2 = OrgParticipantMemberEntity.builder()
                .orgEntity(org2)
                .memberEntity(member)
                .role(Role.ADMIN)
                .status(status)
                .build();

        List<OrgParticipantMemberEntity> participants = Arrays.asList(participant1, participant2);

        given(orgParticipantMemberRepository.findAllByMemberEntity_MemberIdAndStatus(memberId, status))
                .willReturn(participants);

        // when
        List<OrgParticipantMemberEntity> result = orgParticipantMemberRepository.findAllByMemberEntity_MemberIdAndStatus(memberId, status);

        // then
        assertThat(result).hasSize(2);
        assertThat(result)
                .allSatisfy(opm -> assertThat(opm.getStatus()).isEqualTo(status));
    }

    @Test
    @DisplayName("조직, 구성원, 권한, 상태로 존재 여부 확인")
    void existsByOrgEntity_OrgIdAndMemberEntity_MemberIdAndRoleAndStatus() {
        // given
        Long orgId = 1L;
        Long memberId = 1L;
        Role role = Role.ADMIN;
        Status status = Status.ACTIVE;

        given(orgParticipantMemberRepository.existsByOrgEntity_OrgIdAndMemberEntity_MemberIdAndRoleAndStatus(
                orgId, memberId, role, status))
                .willReturn(true);
        given(orgParticipantMemberRepository.existsByOrgEntity_OrgIdAndMemberEntity_MemberIdAndRoleAndStatus(
                orgId, 999L, role, status))
                .willReturn(false);

        // when & then
        assertThat(orgParticipantMemberRepository.existsByOrgEntity_OrgIdAndMemberEntity_MemberIdAndRoleAndStatus(
                orgId, memberId, role, status)).isTrue();
        assertThat(orgParticipantMemberRepository.existsByOrgEntity_OrgIdAndMemberEntity_MemberIdAndRoleAndStatus(
                orgId, 999L, role, status)).isFalse();
    }

    @Test
    @DisplayName("조직 ID와 구성원 ID로 참여 정보 조회")
    void findByOrgEntity_OrgIdAndMemberEntity_MemberId() {
        // given
        Long orgId = 1L;
        Long memberId = 1L;

        OrgEntity org = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        UserEntity user = UserEntity.builder()
                .email("kim@test.com")
                .password("password")
                .build();

        MemberEntity member = MemberEntity.builder()
                .userEntity(user)
                .name("김개발")
                .email("kim@test.com")
                .build();

        OrgParticipantMemberEntity participant = OrgParticipantMemberEntity.builder()
                .orgEntity(org)
                .memberEntity(member)
                .role(Role.MEMBER)
                .status(Status.ACTIVE)
                .build();

        given(orgParticipantMemberRepository.findByOrgEntity_OrgIdAndMemberEntity_MemberId(orgId, memberId))
                .willReturn(participant);

        // when
        OrgParticipantMemberEntity result = orgParticipantMemberRepository.findByOrgEntity_OrgIdAndMemberEntity_MemberId(orgId, memberId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo(Role.MEMBER);
        assertThat(result.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    @DisplayName("권한과 상태별 구성원 수 집계")
    void countByOrgEntity_OrgIdAndRoleAndStatus() {
        // given
        Long orgId = 1L;
        Role role = Role.ROOT_ADMIN;
        Status status = Status.ACTIVE;

        given(orgParticipantMemberRepository.countByOrgEntity_OrgIdAndRoleAndStatus(orgId, role, status))
                .willReturn(1);

        // when
        int result = orgParticipantMemberRepository.countByOrgEntity_OrgIdAndRoleAndStatus(orgId, role, status);

        // then
        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("조직과 구성원 엔티티로 존재 여부 확인")
    void existsByOrgEntityAndMemberEntity() {
        // given
        OrgEntity org = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        UserEntity user = UserEntity.builder()
                .email("kim@test.com")
                .password("password")
                .build();

        MemberEntity member = MemberEntity.builder()
                .userEntity(user)
                .name("김개발")
                .email("kim@test.com")
                .build();

        given(orgParticipantMemberRepository.existsByOrgEntityAndMemberEntity(org, member))
                .willReturn(true);

        // when
        boolean result = orgParticipantMemberRepository.existsByOrgEntityAndMemberEntity(org, member);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("사용자 ID와 상태로 조직 참여 정보 조회")
    void findByOrgEntity_OrgIdAndMemberEntity_UserEntity_UserIdAndStatus() {
        // given
        Long orgId = 1L;
        Long userId = 1L;
        Status status = Status.ACTIVE;

        OrgEntity org = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        UserEntity user = UserEntity.builder()
                .email("kim@test.com")
                .password("password")
                .build();

        MemberEntity member = MemberEntity.builder()
                .userEntity(user)
                .name("김개발")
                .email("kim@test.com")
                .build();

        OrgParticipantMemberEntity participant = OrgParticipantMemberEntity.builder()
                .orgEntity(org)
                .memberEntity(member)
                .role(Role.MEMBER)
                .status(status)
                .build();

        given(orgParticipantMemberRepository.findByOrgEntity_OrgIdAndMemberEntity_UserEntity_UserIdAndStatus(orgId, userId, status))
                .willReturn(Optional.of(participant));

        // when
        Optional<OrgParticipantMemberEntity> result = orgParticipantMemberRepository
                .findByOrgEntity_OrgIdAndMemberEntity_UserEntity_UserIdAndStatus(orgId, userId, status);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo(status);
    }

    @Test
    @DisplayName("조직의 모든 구성원 조회")
    void findAllByOrgEntity_OrgId() {
        // given
        Long orgId = 1L;

        OrgEntity org = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        UserEntity user1 = UserEntity.builder()
                .email("kim@test.com")
                .password("password")
                .build();

        UserEntity user2 = UserEntity.builder()
                .email("park@test.com")
                .password("password")
                .build();

        MemberEntity member1 = MemberEntity.builder()
                .userEntity(user1)
                .name("김개발")
                .email("kim@test.com")
                .build();

        MemberEntity member2 = MemberEntity.builder()
                .userEntity(user2)
                .name("박디자인")
                .email("park@test.com")
                .build();

        OrgParticipantMemberEntity participant1 = OrgParticipantMemberEntity.builder()
                .orgEntity(org)
                .memberEntity(member1)
                .role(Role.MEMBER)
                .status(Status.ACTIVE)
                .build();

        OrgParticipantMemberEntity participant2 = OrgParticipantMemberEntity.builder()
                .orgEntity(org)
                .memberEntity(member2)
                .role(Role.ADMIN)
                .status(Status.ACTIVE)
                .build();

        List<OrgParticipantMemberEntity> participants = Arrays.asList(participant1, participant2);

        given(orgParticipantMemberRepository.findAllByOrgEntity_OrgId(orgId))
                .willReturn(participants);

        // when
        List<OrgParticipantMemberEntity> result = orgParticipantMemberRepository.findAllByOrgEntity_OrgId(orgId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(opm -> opm.getMemberEntity().getName())
                .containsExactly("김개발", "박디자인");
    }

    @Test
    @DisplayName("조직 내 이메일 존재 여부 확인")
    void existsByOrgEntity_OrgIdAndMemberEntity_Email() {
        // given
        Long orgId = 1L;
        String email = "test@example.com";

        given(orgParticipantMemberRepository.existsByOrgEntity_OrgIdAndMemberEntity_Email(orgId, email))
                .willReturn(true);
        given(orgParticipantMemberRepository.existsByOrgEntity_OrgIdAndMemberEntity_Email(orgId, "notfound@example.com"))
                .willReturn(false);

        // when & then
        assertThat(orgParticipantMemberRepository.existsByOrgEntity_OrgIdAndMemberEntity_Email(orgId, email)).isTrue();
        assertThat(orgParticipantMemberRepository.existsByOrgEntity_OrgIdAndMemberEntity_Email(orgId, "notfound@example.com")).isFalse();
    }
}