package com.ourhour.domain.org.service;

import java.util.List;

import com.ourhour.domain.auth.exception.AuthException;
import com.ourhour.domain.member.dto.MemberInfoResDTO;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.org.dto.OrgDetailReqDTO;
import com.ourhour.domain.org.dto.OrgDetailResDTO;
import com.ourhour.domain.org.dto.OrgReqDTO;
import com.ourhour.domain.org.dto.OrgResDTO;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberId;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.org.mapper.OrgMapper;
import com.ourhour.domain.org.mapper.OrgParticipantMemberMapper;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;
import com.ourhour.domain.org.repository.OrgRepository;
import com.ourhour.domain.project.dto.ProjectNameResDTO;
import com.ourhour.domain.project.repository.ProjectParticipantRepository;
import com.ourhour.domain.user.entity.UserEntity;
import com.ourhour.domain.user.repository.UserRepository;
import com.ourhour.global.common.dto.PageResponse;
import com.ourhour.global.exception.BusinessException;

import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.jwt.util.UserContextHolder;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrgService {

    private final OrgMapper orgMapper;
    private final OrgRepository orgRepository;
    private final MemberRepository memberRepository;
    private final OrgParticipantMemberRepository orgParticipantMemberRepository;
    private final ProjectParticipantRepository projectParticipantRepository;
    private final UserRepository userRepository;
    private final OrgParticipantMemberMapper orgParticipantMemberMapper;

    @Transactional
    public OrgResDTO registerOrg(OrgReqDTO orgReqDTO) {

        // 해당 회사를 등록한 유저 정보 가져오기
        Claims claims = UserContextHolder.get();
        Long userId = claims.getUserId();

        // 회사를 등록한 사용자 조회
        UserEntity userEntity = userRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(AuthException::userNotFoundException);

        // OrgReqDTO에서 OrgEntity로 변환
        OrgEntity orgReqEntity = orgMapper.toOrgEntity(orgReqDTO);

        // 회사 등록
        OrgEntity orgEntity = orgRepository.save(orgReqEntity);

        // 해당 회사를 등록한 유저, 멤버 자동 등록
        MemberEntity memberEntity = MemberEntity.builder()
                .userEntity(userEntity)
                .name(orgReqDTO.getMemberName())
                .email(userEntity.getEmail())
                .build();
        memberRepository.save(memberEntity);

        // 해당 회사의 루트 관리자 권한 부여
        OrgParticipantMemberEntity orgParticipantMemberEntity = OrgParticipantMemberEntity.builder()
                .orgEntity(orgEntity)
                .memberEntity(memberEntity)
                .role(Role.ROOT_ADMIN)
                .build();
        orgParticipantMemberRepository.save(orgParticipantMemberEntity);

        // 응답 DTO로 변환
        OrgResDTO orgResDTO = orgParticipantMemberMapper.toOrgResDTO(orgEntity, memberEntity, orgParticipantMemberEntity);

        return orgResDTO;

    }

    // 회사 정보 조회
    public OrgDetailResDTO getOrgInfo(Long orgId) {

        if (orgId <= 0) {
            throw BusinessException.badRequest("유효하지 않은 회사 ID입니다.");
        }

        OrgEntity orgEntity = orgRepository.findById(orgId)
                .orElseThrow(() -> BusinessException.badRequest("존재하지 않는 회사 ID 입니다: " + orgId));

        OrgDetailResDTO orgDetailResDTO = orgMapper.toOrgDetailResDTO(orgEntity);

        return orgDetailResDTO;
    }

    // 회사 정보 수정
    @Transactional
    public OrgDetailResDTO updateOrg(Long orgId, OrgDetailReqDTO orgDetailReqDTO) {

        if (orgId <= 0) {
            throw BusinessException.badRequest("유효하지 않은 회사 ID입니다.");
        }

        OrgEntity orgEntity = orgRepository.findById(orgId)
                .orElseThrow(() -> BusinessException.badRequest("존재하지 않는 회사 ID 입니다: " + orgId));

        orgEntity.updateInfo(orgDetailReqDTO);

        OrgEntity updatedOrgEntity = orgRepository.save(orgEntity);

        return orgMapper.toOrgDetailResDTO(updatedOrgEntity);

    }

    // 회사 구성원 목록 조회
    public PageResponse<MemberInfoResDTO> getOrgMembers(Long orgId, Pageable pageable) {

        if (orgId == null || orgId <= 0) {
            throw BusinessException.badRequest("유효하지 않은 회사 ID입니다.");
        }

        // 회사 존재 여부 확인
        if (!orgRepository.existsById(orgId)) {
            throw BusinessException.badRequest("존재하지 않는 회사 ID 입니다: " + orgId);
        }

        Page<MemberInfoResDTO> memberInfoPage = orgParticipantMemberRepository.findByOrgId(orgId, pageable);

        return PageResponse.of(memberInfoPage);
    }

    // 회사 구성원 상세 조회


    // 구성원 삭제
    @Transactional
    public void deleteOrgMember(Long orgId, Long memberId) {
        orgParticipantMemberRepository.deleteById(new OrgParticipantMemberId(orgId, memberId));
    }

    // 회사 삭제
    @Transactional
    public void deleteOrg(Long orgId) {
        orgRepository.deleteById(orgId);
    }

    // 본인이 참여 중인 프로젝트 이름 목록 조회(좌측 사이드바)
    public List<ProjectNameResDTO> getMyProjects(Long orgId, Long memberId) {

        if (orgId == null || orgId <= 0) {
            throw BusinessException.badRequest("유효하지 않은 조직 ID입니다.");
        }

        if (memberId == null || memberId <= 0) {
            throw BusinessException.badRequest("유효하지 않은 멤버 ID입니다.");
        }

        OrgParticipantMemberId participantId = new OrgParticipantMemberId(orgId, memberId);
        if (!orgParticipantMemberRepository.existsById(participantId)) {
            throw BusinessException.forbidden("해당 조직의 구성원이 아닙니다.");
        }

        return projectParticipantRepository.findMemberProjectsByOrg(memberId, orgId);
    }
}
