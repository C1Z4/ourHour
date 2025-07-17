package com.ourhour.domain.org.service;

import com.ourhour.domain.member.dto.MemberInfoResDTO;
import com.ourhour.domain.member.exceptions.MemberException;
import com.ourhour.domain.org.dto.OrgMemberRoleReqDTO;
import com.ourhour.domain.org.dto.OrgMemberRoleResDTO;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberId;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.org.mapper.OrgParticipantMemberMapper;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;
import com.ourhour.domain.org.repository.OrgRepository;
import com.ourhour.global.common.dto.PageResponse;
import com.ourhour.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ourhour.domain.org.enums.Status.ACTIVE;

@Service
@RequiredArgsConstructor
public class OrgMemberService {

    private final OrgRepository orgRepository;
    private final OrgParticipantMemberRepository orgParticipantMemberRepository;
    private final OrgParticipantMemberMapper orgParticipantMemberMapper;
    private final OrgRoleGuardService orgRoleGuardService;

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
    public MemberInfoResDTO getOrgMember(Long orgId, Long memberId) {

        MemberInfoResDTO memberInfoResDTO = orgParticipantMemberRepository.findByOrgIdAndMemberId(orgId, memberId);

        return memberInfoResDTO;

    }

    // 구성원 권한 변경
    @Transactional
    public OrgMemberRoleResDTO changeRole(Long orgId, Long memberId, OrgMemberRoleReqDTO orgMemberRoleReqDTO) {

        // 대상 멤버 조회
        OrgParticipantMemberEntity orgParticipantMemberEntity = orgParticipantMemberRepository
                .findByOrgEntity_OrgIdAndMemberEntity_MemberIdAndStatus(orgId, memberId, ACTIVE)
                .orElseThrow(MemberException::memberNotFoundException);

        Role oldRole = orgParticipantMemberEntity.getRole();
        Role newRole = orgMemberRoleReqDTO.getNewRole();
        int currentRootAdminCount = orgParticipantMemberRepository.countRootAdmins(orgId);

        // 동일 권한 변경일 경우 early return(정책 위반 아님)
        if (oldRole == newRole) {
            return orgParticipantMemberMapper.toOrgMemberRoleResDTO(orgParticipantMemberEntity, oldRole, currentRootAdminCount);
        }

        // 루트 관리자 정책 검사
        orgRoleGuardService.assertRoleChangeAllowed(oldRole, newRole, currentRootAdminCount);

        // 구성원 권한 변경
        orgParticipantMemberEntity.changeRole(newRole);

        int afterRootAdminCount = currentRootAdminCount
                + (oldRole == Role.ROOT_ADMIN ? -1 : 0)
                + (newRole == Role.ROOT_ADMIN ?  1 : 0);

        // 엔티티 -> DTO 변환
        OrgMemberRoleResDTO orgMemberRoleResDTO = orgParticipantMemberMapper.toOrgMemberRoleResDTO(orgParticipantMemberEntity, oldRole, afterRootAdminCount);

        return orgMemberRoleResDTO;

    }

    // 구성원 삭제
    @Transactional
    public void deleteOrgMember(Long orgId, Long memberId) {


        orgParticipantMemberRepository.deleteById(new OrgParticipantMemberId(orgId, memberId));
    }
}
