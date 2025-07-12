package com.ourhour.domain.org.service;

import java.util.List;

import com.ourhour.domain.member.dto.MemberInfoResDTO;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.org.dto.OrgReqDTO;
import com.ourhour.domain.org.dto.OrgResDTO;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberId;
import com.ourhour.domain.org.mapper.OrgMapper;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;
import com.ourhour.domain.org.repository.OrgRepository;
import com.ourhour.domain.project.dto.ProjectNameResDTO;
import com.ourhour.domain.project.repository.ProjectParticipantRepository;
import com.ourhour.global.common.dto.PageResponse;
import com.ourhour.global.exception.BusinessException;

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

    @Transactional
    public OrgResDTO registerOrg(OrgReqDTO orgReqDTO) {

        // OrgReqDTO에서 OrgEntity로 변환
        OrgEntity orgReqEntity = orgMapper.toOrgEntity(orgReqDTO);

        // 데이터베이스에 등록 - 회사 생성
        OrgEntity orgEntity = orgRepository.save(orgReqEntity);

        // TODO: 루트 관리자 권한 추가 예정
        /*
         * // 루트 관리자가 될 MemberEntity 조회
         * MemberEntity memberEntity = memberRepository.findById(memberId)
         * .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 ID 입니다: " +
         * memberId));
         * 
         * // 해당 회사에 대한 루트 관리자 권한 생성
         * OrgParticipantMemberId orgParticipantMemberId = new
         * OrgParticipantMemberId(orgEntity.getOrgId(), memberEntity.getMemberId());
         * OrgParticipantMemberEntity orgParticipantMemberEntity =
         * OrgParticipantMemberEntity.builder()
         * .orgParticipantMemberId(orgParticipantMemberId)
         * .orgEntity(orgEntity)
         * .memberEntity(memberEntity)
         * .departmentEntity(null)
         * .positionEntity(null)
         * .role(Role.ROOT_ADMIN)
         * .build();
         * 
         * // 데이터베이스에 등록 - 해당 회사의 루트 관리자 권한
         * orgParticipantMemberRepository.save(orgParticipantMemberEntity);
         */

        // 응답 DTO로 변환
        OrgResDTO orgResDTO = orgMapper.toOrgResDTO(orgEntity);

        return orgResDTO;
    }

    // 회사 정보 조회
    public OrgResDTO getOrgInfo(Long orgId) {

        if (orgId <= 0) {
            throw BusinessException.badRequest("유효하지 않은 회사 ID입니다.");
        }

        OrgEntity orgEntity = orgRepository.findById(orgId)
                .orElseThrow(() -> BusinessException.badRequest("존재하지 않는 회사 ID 입니다: " + orgId));

        OrgResDTO orgResDTO = orgMapper.toOrgResDTO(orgEntity);

        return orgResDTO;
    }

    // 회사 정보 수정
    @Transactional
    public OrgResDTO updateOrg(Long orgId, OrgReqDTO orgReqDTO) {

        if (orgId <= 0) {
            throw BusinessException.badRequest("유효하지 않은 회사 ID입니다.");
        }

        OrgEntity orgEntity = orgRepository.findById(orgId)
                .orElseThrow(() -> BusinessException.badRequest("존재하지 않는 회사 ID 입니다: " + orgId));

        orgMapper.updateOrgEntity(orgEntity, orgReqDTO);

        OrgEntity updatedOrgEntity = orgRepository.save(orgEntity);

        return orgMapper.toOrgResDTO(updatedOrgEntity);
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
