package com.ourhour.domain.member.service;

import com.ourhour.domain.member.exceptions.MemberOrgException;
import com.ourhour.domain.member.dto.MemberOrgDetailResDTO;
import com.ourhour.domain.member.dto.MemberOrgSummaryResDTO;
import com.ourhour.domain.member.mapper.MemberOrgMapper;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberOrgMapper memberOrgMapper;

    public List<MemberOrgSummaryResDTO> findOrgListByMemberId(Long memberId) {

        // memberId를 통해 본인이 속한 회사 목록 조회
        List<OrgParticipantMemberEntity> orgParticipantMemberEntityList = memberRepository.findOrgListByMemberId(memberId);

        // OrgParticipantMemberEntity에서 MemberOrgSummaryResDTO로 변환
        List<MemberOrgSummaryResDTO> memberOrgSummaryResDTOList =  memberOrgMapper.toMemberOrgSummaryResDTOList(orgParticipantMemberEntityList);

        return memberOrgSummaryResDTOList;

    }

    public MemberOrgDetailResDTO findOrgDetailByMemberIdAndOrgId(Long memberId, Long orgId) {

        // memberId와 orgId를 통해 본인이 속한 회사 상세 정보 조회
        OrgParticipantMemberEntity orgDetailEntity = memberRepository
                .findOrgDetailByMemberIdAndOrgId(memberId, orgId)
                .orElseThrow(MemberOrgException::orgNotFoundException);

        // OrgParticipantMemberEntity -> MemberOrgDetailResDTO 변환
        MemberOrgDetailResDTO memberOrgDetailResDTO = memberOrgMapper.toMemberOrgDetailResDTO(orgDetailEntity);

        return memberOrgDetailResDTO;

    }
}