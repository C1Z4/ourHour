package com.ourhour.domain.org.service;

import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.org.dto.OrgReqDTO;
import com.ourhour.domain.org.dto.OrgResDTO;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.mapper.OrgMapper;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;
import com.ourhour.domain.org.repository.OrgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrgService {

    private final OrgMapper orgMapper;
    private final OrgRepository orgRepository;
    private final MemberRepository memberRepository;
    private final OrgParticipantMemberRepository orgParticipantMemberRepository;

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
        OrgEntity orgEntity = orgRepository.findById(orgId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회사 ID 입니다: " + orgId));

        OrgResDTO orgResDTO = orgMapper.toOrgResDTO(orgEntity);

        return orgResDTO;
    }
}
