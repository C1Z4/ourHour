package com.ourhour.domain.member.service;

import com.ourhour.global.exception.BusinessException;
import com.ourhour.domain.member.dto.MemberOrgDetailResDTO;
import com.ourhour.domain.member.dto.MemberOrgSummaryResDTO;
import com.ourhour.domain.member.mapper.MemberOrgMapper;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ourhour.global.common.dto.PageResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberOrgMapper memberOrgMapper;

    public MemberOrgSummaryResDTO findOrgSummaryByMemberId(Long memberId) {
        OrgParticipantMemberEntity orgParticipantMemberEntity = memberRepository.findOrgByMemberId(memberId);

        return memberOrgMapper.toMemberOrgSummaryResDTO(orgParticipantMemberEntity);
    }

    public PageResponse<MemberOrgSummaryResDTO> findOrgSummaryByMemberId(Long memberId, Pageable pageable) {

        Page<OrgParticipantMemberEntity> orgParticipantMemberEntityPage = memberRepository.findOrgListByMemberId(memberId, pageable);

        if (orgParticipantMemberEntityPage.isEmpty()) {
            return PageResponse.empty(pageable.getPageNumber() + 1, pageable.getPageSize());
        }

        Page<MemberOrgSummaryResDTO> dtoPage = orgParticipantMemberEntityPage.map(memberOrgMapper::toMemberOrgSummaryResDTO);

        return PageResponse.of(dtoPage);
    }

    public PageResponse<MemberOrgSummaryResDTO> findOrgSummaryByMemberIds(List<Long> memberIds, Pageable pageable) {

        Page<OrgParticipantMemberEntity> orgParticipantMemberEntityPage = memberRepository.findOrgListByMemberIds(memberIds, pageable);

        if (orgParticipantMemberEntityPage.isEmpty()) {
            return PageResponse.empty(pageable.getPageNumber() + 1, pageable.getPageSize());
        }

        Page<MemberOrgSummaryResDTO> dtoPage = orgParticipantMemberEntityPage.map(memberOrgMapper::toMemberOrgSummaryResDTO);

        return PageResponse.of(dtoPage);
    }

    public MemberOrgDetailResDTO findOrgDetailByMemberIdAndOrgId(Long memberId, Long orgId) {

        // memberId와 orgId를 통해 본인이 속한 회사 상세 정보 조회
        OrgParticipantMemberEntity orgDetailEntity = memberRepository
                .findOrgDetailByMemberIdAndOrgId(memberId, orgId)
                .orElseThrow(() -> BusinessException.badRequest("해당 회사 정보를 찾을 수 없습니다."));

        // OrgParticipantMemberEntity -> MemberOrgDetailResDTO 변환
        MemberOrgDetailResDTO memberOrgDetailResDTO = memberOrgMapper.toMemberOrgDetailResDTO(orgDetailEntity);

        return memberOrgDetailResDTO;

    }
}