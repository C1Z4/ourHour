package com.ourhour.domain.org.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;

import java.time.LocalDate;
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

import com.ourhour.domain.auth.exception.AuthException;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.org.dto.OrgDetailReqDTO;
import com.ourhour.domain.org.dto.OrgDetailResDTO;
import com.ourhour.domain.org.dto.OrgReqDTO;
import com.ourhour.domain.org.dto.OrgResDTO;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.org.enums.Status;
import com.ourhour.domain.org.exception.OrgException;
import com.ourhour.domain.org.mapper.OrgMapper;
import com.ourhour.domain.org.mapper.OrgParticipantMemberMapper;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;
import com.ourhour.domain.org.repository.OrgRepository;
import com.ourhour.domain.project.dto.ProjectNameResDTO;
import com.ourhour.domain.project.repository.ProjectParticipantRepository;
import com.ourhour.domain.user.entity.UserEntity;
import com.ourhour.domain.user.repository.UserRepository;
import com.ourhour.domain.user.service.AnonymizeUserService;
import com.ourhour.global.common.service.ImageService;
import com.ourhour.global.util.SecurityUtil;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrgService 테스트")
class OrgServiceTest {

    @InjectMocks
    private OrgService orgService;

    @Mock
    private OrgMapper orgMapper;

    @Mock
    private OrgRepository orgRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private OrgParticipantMemberRepository orgParticipantMemberRepository;

    @Mock
    private ProjectParticipantRepository projectParticipantRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrgParticipantMemberMapper orgParticipantMemberMapper;

    @Mock
    private OrgRoleGuardService orgRoleGuardService;

    @Mock
    private AnonymizeUserService anonymizeUserService;

    @Mock
    private ImageService imageService;

    @Test
    @DisplayName("조직 등록 성공")
    void registerOrg_Success() {
        // given
        Long userId = 1L;
        OrgReqDTO orgReqDTO = new OrgReqDTO();
        orgReqDTO.setName("테스트 조직");
        orgReqDTO.setEmail("test@org.com");
        orgReqDTO.setMemberName("김대표");

        UserEntity userEntity = UserEntity.builder()
                .email("user@test.com")
                .password("password")
                .build();

        OrgEntity orgEntity = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        MemberEntity memberEntity = MemberEntity.builder()
                .userEntity(userEntity)
                .name("김대표")
                .email("user@test.com")
                .build();

        OrgParticipantMemberEntity participant = OrgParticipantMemberEntity.builder()
                .orgEntity(orgEntity)
                .memberEntity(memberEntity)
                .role(Role.ROOT_ADMIN)
                .status(Status.ACTIVE)
                .joinedAt(LocalDate.now())
                .build();

        OrgResDTO orgResDTO = OrgResDTO.builder()
                .orgId(1L)
                .name("테스트 조직")
                .build();

        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUserId).thenReturn(userId);

            given(userRepository.findByUserIdAndIsDeletedFalse(userId)).willReturn(Optional.of(userEntity));
            given(orgMapper.toOrgEntity(any(OrgReqDTO.class))).willReturn(orgEntity);
            given(orgRepository.save(any(OrgEntity.class))).willReturn(orgEntity);
            given(memberRepository.save(any(MemberEntity.class))).willReturn(memberEntity);
            given(orgParticipantMemberRepository.save(any(OrgParticipantMemberEntity.class))).willReturn(participant);
            given(orgParticipantMemberMapper.toOrgResDTO(any(), any(), any())).willReturn(orgResDTO);

            // when
            OrgResDTO result = orgService.registerOrg(orgReqDTO);

            // then
            assertThat(result.getName()).isEqualTo("테스트 조직");
            then(userRepository).should().findByUserIdAndIsDeletedFalse(userId);
            then(orgRepository).should().save(any(OrgEntity.class));
            then(memberRepository).should().save(any(MemberEntity.class));
            then(orgParticipantMemberRepository).should().save(any(OrgParticipantMemberEntity.class));
        }
    }

    @Test
    @DisplayName("조직 등록 실패 - 사용자를 찾을 수 없음")
    void registerOrg_UserNotFound() {
        // given
        Long userId = 1L;
        OrgReqDTO orgReqDTO = new OrgReqDTO();
        orgReqDTO.setName("테스트 조직");

        try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
            securityUtilMock.when(SecurityUtil::getCurrentUserId).thenReturn(userId);

            given(userRepository.findByUserIdAndIsDeletedFalse(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orgService.registerOrg(orgReqDTO))
                    .isInstanceOf(AuthException.class);
        }
    }

    @Test
    @DisplayName("조직 정보 조회 성공")
    void getOrgInfo_Success() {
        // given
        Long orgId = 1L;

        OrgEntity orgEntity = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        OrgDetailResDTO orgDetailResDTO = OrgDetailResDTO.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .build();

        given(orgRepository.findById(orgId)).willReturn(Optional.of(orgEntity));
        given(orgMapper.toOrgDetailResDTO(orgEntity)).willReturn(orgDetailResDTO);

        // when
        OrgDetailResDTO result = orgService.getOrgInfo(orgId);

        // then
        assertThat(result.getName()).isEqualTo("테스트 조직");
        assertThat(result.getEmail()).isEqualTo("test@org.com");
    }

    @Test
    @DisplayName("조직 정보 조회 실패 - 조직을 찾을 수 없음")
    void getOrgInfo_OrgNotFound() {
        // given
        Long orgId = 1L;

        given(orgRepository.findById(orgId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orgService.getOrgInfo(orgId))
                .isInstanceOf(OrgException.class);
    }

    @Test
    @DisplayName("조직 정보 수정 성공")
    void updateOrg_Success() {
        // given
        Long orgId = 1L;
        OrgDetailReqDTO reqDTO = new OrgDetailReqDTO();
        reqDTO.setName("수정된 조직명");
        reqDTO.setEmail("updated@org.com");

        OrgEntity orgEntity = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        OrgEntity updatedOrg = OrgEntity.builder()
                .name("수정된 조직명")
                .email("updated@org.com")
                .phone("010-1234-5678")
                .build();

        OrgDetailResDTO orgDetailResDTO = OrgDetailResDTO.builder()
                .name("수정된 조직명")
                .email("updated@org.com")
                .build();

        given(orgRepository.findById(orgId)).willReturn(Optional.of(orgEntity));
        given(orgRepository.save(orgEntity)).willReturn(updatedOrg);
        given(orgMapper.toOrgDetailResDTO(updatedOrg)).willReturn(orgDetailResDTO);

        // when
        OrgDetailResDTO result = orgService.updateOrg(orgId, reqDTO);

        // then
        assertThat(result.getName()).isEqualTo("수정된 조직명");
        assertThat(result.getEmail()).isEqualTo("updated@org.com");
    }

    @Test
    @DisplayName("조직 삭제")
    void deleteOrg() {
        // given
        Long orgId = 1L;

        // when
        orgService.deleteOrg(orgId);

        // then
        then(orgRepository).should().deleteById(orgId);
    }

    @Test
    @DisplayName("내 프로젝트 목록 조회")
    void getMyProjects() {
        // given
        Long memberId = 1L;
        Long orgId = 1L;

        ProjectNameResDTO project1 = ProjectNameResDTO.builder()
                .projectId(1L)
                .name("프로젝트 A")
                .build();

        ProjectNameResDTO project2 = ProjectNameResDTO.builder()
                .projectId(2L)
                .name("프로젝트 B")
                .build();

        List<ProjectNameResDTO> projects = Arrays.asList(project1, project2);

        given(projectParticipantRepository.findMemberProjectsByOrg(memberId, orgId)).willReturn(projects);

        // when
        List<ProjectNameResDTO> result = orgService.getMyProjects(memberId, orgId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("프로젝트 A");
        assertThat(result.get(1).getName()).isEqualTo("프로젝트 B");
    }
}