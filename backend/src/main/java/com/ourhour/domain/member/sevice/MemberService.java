package com.ourhour.domain.member.sevice;

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

        List<OrgParticipantMemberEntity> orgParticipantMemberEntityList = memberRepository.findOrgListByMemberId(memberId);

        // OrgParticipantMemberEntity에서 MemberOrgSummaryResDTO로 변환
        List<MemberOrgSummaryResDTO> memberOrgSummaryResDTOList =  memberOrgMapper.toMemberOrgSummaryResDTOList(orgParticipantMemberEntityList);

        return memberOrgSummaryResDTOList;

    }
}
