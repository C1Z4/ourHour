package com.ourhour.domain.org.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.ourhour.domain.member.dto.MemberInfoResDTO;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.exception.MemberException;
import com.ourhour.domain.org.dto.OrgMemberRoleReqDTO;
import com.ourhour.domain.org.dto.OrgMemberRoleResDTO;
import com.ourhour.domain.org.entity.DepartmentEntity;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.org.entity.PositionEntity;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.org.enums.Status;
import com.ourhour.domain.org.exception.OrgException;
import com.ourhour.domain.org.mapper.OrgParticipantMemberMapper;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;
import com.ourhour.domain.org.repository.OrgRepository;
import com.ourhour.domain.user.entity.UserEntity;
import com.ourhour.domain.user.service.AnonymizeUserService;
import com.ourhour.global.common.dto.PageResponse;
import com.ourhour.global.util.SecurityUtil;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrgMemberService 테스트")
class OrgMemberServiceTest {

    @InjectMocks
    private OrgMemberService orgMemberService;

    @Mock
    private OrgRepository orgRepository;

    @Mock
    private OrgParticipantMemberRepository orgParticipantMemberRepository;

    @Mock
    private OrgParticipantMemberMapper orgParticipantMemberMapper;

    @Mock
    private OrgRoleGuardService orgRoleGuardService;

    @Mock
    private AnonymizeUserService anonymizeUserService;

    @Test
    @DisplayName("조직 구성원 목록 조회 성공")
    void getOrgMembers_Success() {
        // given
        Long orgId = 1L;
        String search = null;
        Pageable pageable = PageRequest.of(0, 10);

        UserEntity user1 = UserEntity.builder()
                .email("kim@test.com")
                .password("password")
                .build();

        MemberEntity member1 = MemberEntity.builder()
                .userEntity(user1)
                .name("김개발")
                .email("kim@test.com")
                .build();

        OrgEntity org = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        OrgParticipantMemberEntity participant1 = OrgParticipantMemberEntity.builder()
                .orgEntity(org)
                .memberEntity(member1)
                .role(Role.MEMBER)
                .status(Status.ACTIVE)
                .build();

        List<OrgParticipantMemberEntity> participants = Arrays.asList(participant1);
        Page<OrgParticipantMemberEntity> participantPage = new PageImpl<>(participants, pageable, participants.size());

        given(orgRepository.existsById(orgId)).willReturn(true);
        given(orgParticipantMemberRepository.findByOrgId(orgId, pageable)).willReturn(participantPage);

        // when
        PageResponse<MemberInfoResDTO> result = orgMemberService.getOrgMembers(orgId, search, pageable);

        // then
        assertThat(result.getData()).hasSize(1);
        then(orgRepository).should().existsById(orgId);
        then(orgParticipantMemberRepository).should().findByOrgId(orgId, pageable);
    }

    @Test
    @DisplayName("조직 구성원 목록 조회 - 검색어 포함")
    void getOrgMembers_WithSearch() {
        // given
        Long orgId = 1L;
        String search = "김";
        Pageable pageable = PageRequest.of(0, 10);

        UserEntity user1 = UserEntity.builder()
                .email("kim@test.com")
                .password("password")
                .build();

        MemberEntity member1 = MemberEntity.builder()
                .userEntity(user1)
                .name("김개발")
                .email("kim@test.com")
                .build();

        OrgEntity org = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        OrgParticipantMemberEntity participant1 = OrgParticipantMemberEntity.builder()
                .orgEntity(org)
                .memberEntity(member1)
                .role(Role.MEMBER)
                .status(Status.ACTIVE)
                .build();

        List<OrgParticipantMemberEntity> participants = Arrays.asList(participant1);
        Page<OrgParticipantMemberEntity> participantPage = new PageImpl<>(participants, pageable, participants.size());

        given(orgRepository.existsById(orgId)).willReturn(true);
        given(orgParticipantMemberRepository.findByOrgIdAndNameContaining(orgId, search, pageable)).willReturn(participantPage);

        // when
        PageResponse<MemberInfoResDTO> result = orgMemberService.getOrgMembers(orgId, search, pageable);

        // then
        assertThat(result.getData()).hasSize(1);
        then(orgParticipantMemberRepository).should().findByOrgIdAndNameContaining(orgId, search, pageable);
    }

    @Test
    @DisplayName("조직 구성원 목록 조회 실패 - 조직을 찾을 수 없음")
    void getOrgMembers_OrgNotFound() {
        // given
        Long orgId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        given(orgRepository.existsById(orgId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> orgMemberService.getOrgMembers(orgId, null, pageable))
                .isInstanceOf(OrgException.class);
    }

    @Test
    @DisplayName("조직 구성원 상세 조회")
    void getOrgMember() {
        // given
        Long orgId = 1L;
        Long memberId = 1L;

        UserEntity user = UserEntity.builder()
                .email("kim@test.com")
                .password("password")
                .build();

        MemberEntity member = MemberEntity.builder()
                .userEntity(user)
                .name("김개발")
                .email("kim@test.com")
                .build();

        OrgEntity org = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        OrgParticipantMemberEntity participant = OrgParticipantMemberEntity.builder()
                .orgEntity(org)
                .memberEntity(member)
                .role(Role.MEMBER)
                .status(Status.ACTIVE)
                .build();

        MemberInfoResDTO memberInfoResDTO = new MemberInfoResDTO();
        memberInfoResDTO.setName("김개발");
        memberInfoResDTO.setEmail("kim@test.com");

        given(orgParticipantMemberRepository.findByOrgEntity_OrgIdAndMemberEntity_MemberId(orgId, memberId))
                .willReturn(participant);
        given(orgParticipantMemberMapper.toMemberInfoResDTO(participant)).willReturn(memberInfoResDTO);

        // when
        MemberInfoResDTO result = orgMemberService.getOrgMember(orgId, memberId);

        // then
        assertThat(result.getName()).isEqualTo("김개발");
        assertThat(result.getEmail()).isEqualTo("kim@test.com");
    }

    @Test
    @DisplayName("부서별 구성원 조회")
    void getMembersByDepartment() {
        // given
        Long orgId = 1L;
        Long deptId = 1L;

        UserEntity user = UserEntity.builder()
                .email("kim@test.com")
                .password("password")
                .build();

        MemberEntity member = MemberEntity.builder()
                .userEntity(user)
                .name("김개발")
                .email("kim@test.com")
                .build();

        OrgEntity org = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        DepartmentEntity department = DepartmentEntity.builder()
                .name("개발팀")
                .orgEntity(org)
                .build();

        OrgParticipantMemberEntity participant = OrgParticipantMemberEntity.builder()
                .orgEntity(org)
                .memberEntity(member)
                .departmentEntity(department)
                .role(Role.MEMBER)
                .status(Status.ACTIVE)
                .build();

        List<OrgParticipantMemberEntity> participants = Arrays.asList(participant);

        given(orgRepository.existsById(orgId)).willReturn(true);
        given(orgParticipantMemberRepository.findByOrgIdAndDeptId(orgId, deptId)).willReturn(participants);

        // when
        List<MemberInfoResDTO> result = orgMemberService.getMembersByDepartment(orgId, deptId);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("직책별 구성원 조회")
    void getMembersByPosition() {
        // given
        Long orgId = 1L;
        Long positionId = 1L;

        UserEntity user = UserEntity.builder()
                .email("kim@test.com")
                .password("password")
                .build();

        MemberEntity member = MemberEntity.builder()
                .userEntity(user)
                .name("김팀장")
                .email("kim@test.com")
                .build();

        OrgEntity org = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        PositionEntity position = PositionEntity.builder()
                .name("팀장")
                .orgEntity(org)
                .build();

        OrgParticipantMemberEntity participant = OrgParticipantMemberEntity.builder()
                .orgEntity(org)
                .memberEntity(member)
                .positionEntity(position)
                .role(Role.ADMIN)
                .status(Status.ACTIVE)
                .build();

        List<OrgParticipantMemberEntity> participants = Arrays.asList(participant);

        given(orgRepository.existsById(orgId)).willReturn(true);
        given(orgParticipantMemberRepository.findByOrgIdAndPositionId(orgId, positionId)).willReturn(participants);

        // when
        List<MemberInfoResDTO> result = orgMemberService.getMembersByPosition(orgId, positionId);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("구성원 권한 변경 성공")
    void changeRole_Success() {
        // given
        Long orgId = 1L;
        Long memberId = 1L;
        OrgMemberRoleReqDTO reqDTO = new OrgMemberRoleReqDTO();
        reqDTO.setRole(Role.ADMIN);

        UserEntity user = UserEntity.builder()
                .email("kim@test.com")
                .password("password")
                .build();

        MemberEntity member = MemberEntity.builder()
                .userEntity(user)
                .name("김개발")
                .email("kim@test.com")
                .build();

        OrgEntity org = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        OrgParticipantMemberEntity participant = OrgParticipantMemberEntity.builder()
                .orgEntity(org)
                .memberEntity(member)
                .role(Role.MEMBER)
                .status(Status.ACTIVE)
                .build();

        OrgMemberRoleResDTO roleResDTO = OrgMemberRoleResDTO.builder()
                .role(Role.ADMIN)
                .rootAdminCount(2)
                .build();

        given(orgParticipantMemberRepository.findByOrgEntity_OrgIdAndMemberEntity_MemberIdAndStatus(orgId, memberId, Status.ACTIVE))
                .willReturn(Optional.of(participant));
        given(orgParticipantMemberRepository.countRootAdmins(orgId)).willReturn(2);
        given(orgParticipantMemberMapper.toOrgMemberRoleResDTO(any(), any(), any(Integer.class))).willReturn(roleResDTO);

        // when
        OrgMemberRoleResDTO result = orgMemberService.changeRole(orgId, memberId, reqDTO);

        // then
        assertThat(result.getRole()).isEqualTo(Role.ADMIN);
        then(orgRoleGuardService).should().assertRoleChangeAllowed(Role.MEMBER, Role.ADMIN, 2);
    }

    @Test
    @DisplayName("구성원 권한 변경 실패 - 구성원을 찾을 수 없음")
    void changeRole_MemberNotFound() {
        // given
        Long orgId = 1L;
        Long memberId = 1L;
        OrgMemberRoleReqDTO reqDTO = new OrgMemberRoleReqDTO();
        reqDTO.setRole(Role.ADMIN);

        given(orgParticipantMemberRepository.findByOrgEntity_OrgIdAndMemberEntity_MemberIdAndStatus(orgId, memberId, Status.ACTIVE))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orgMemberService.changeRole(orgId, memberId, reqDTO))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("구성원 삭제 성공")
    void deleteOrgMember_Success() {
        // given
        Long orgId = 1L;
        Long memberId = 1L;

        UserEntity user = UserEntity.builder()
                .email("kim@test.com")
                .password("password")
                .build();

        MemberEntity member = MemberEntity.builder()
                .userEntity(user)
                .name("김개발")
                .email("kim@test.com")
                .build();

        OrgEntity org = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        OrgParticipantMemberEntity participant = OrgParticipantMemberEntity.builder()
                .orgEntity(org)
                .memberEntity(member)
                .role(Role.MEMBER)
                .status(Status.ACTIVE)
                .build();

        given(orgParticipantMemberRepository.findByOrgEntity_OrgIdAndMemberEntity_MemberIdAndStatus(orgId, memberId, Status.ACTIVE))
                .willReturn(Optional.of(participant));

        // when
        orgMemberService.deleteOrgMember(orgId, memberId);

        // then
        then(orgRoleGuardService).should().assertNotLastRootAdminInOrg(orgId, memberId);
        then(anonymizeUserService).should().anonymizeMember(participant);
    }

    @Test
    @DisplayName("조직 나가기 성공")
    void exitOrg_Success() {
        // given
        Long orgId = 1L;
        Long userId = 1L;

        UserEntity user = UserEntity.builder()
                .email("kim@test.com")
                .password("password")
                .build();

        MemberEntity member = MemberEntity.builder()
                .userEntity(user)
                .name("김개발")
                .email("kim@test.com")
                .build();

        OrgEntity org = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        OrgParticipantMemberEntity participant = OrgParticipantMemberEntity.builder()
                .orgEntity(org)
                .memberEntity(member)
                .role(Role.MEMBER)
                .status(Status.ACTIVE)
                .build();

        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUserId).thenReturn(userId);

            given(orgParticipantMemberRepository.findByOrgEntity_OrgIdAndMemberEntity_UserEntity_UserIdAndStatus(orgId, userId, Status.ACTIVE))
                    .willReturn(Optional.of(participant));

            // when
            orgMemberService.exitOrg(orgId);

            // then
            then(orgRoleGuardService).should().assertNotLastRootAdminInOrg(participant);
            then(anonymizeUserService).should().anonymizeMember(participant);
        }
    }

    @Test
    @DisplayName("모든 조직 구성원 조회")
    void getAllOrgMembers() {
        // given
        Long orgId = 1L;

        UserEntity user1 = UserEntity.builder()
                .email("kim@test.com")
                .password("password")
                .build();

        MemberEntity member1 = MemberEntity.builder()
                .userEntity(user1)
                .name("김개발")
                .email("kim@test.com")
                .build();

        OrgEntity org = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        OrgParticipantMemberEntity participant1 = OrgParticipantMemberEntity.builder()
                .orgEntity(org)
                .memberEntity(member1)
                .role(Role.MEMBER)
                .status(Status.ACTIVE)
                .build();

        List<OrgParticipantMemberEntity> participants = Arrays.asList(participant1);

        MemberInfoResDTO memberInfoResDTO = new MemberInfoResDTO();
        memberInfoResDTO.setName("김개발");
        memberInfoResDTO.setEmail("kim@test.com");

        given(orgRepository.existsById(orgId)).willReturn(true);
        given(orgParticipantMemberRepository.findAllByOrgEntity_OrgId(orgId)).willReturn(participants);
        given(orgParticipantMemberMapper.toMemberInfoResDTO(any())).willReturn(memberInfoResDTO);

        // when
        List<MemberInfoResDTO> result = orgMemberService.getAllOrgMembers(orgId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("김개발");
    }
}