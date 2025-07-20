package com.ourhour.domain.member.service;

import com.ourhour.domain.member.dto.MyMemberInfoReqDTO;
import com.ourhour.domain.member.dto.MyMemberInfoResDTO;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.exceptions.MemberException;
import com.ourhour.domain.org.entity.DepartmentEntity;
import com.ourhour.domain.org.entity.PositionEntity;
import com.ourhour.domain.org.enums.Status;
import com.ourhour.domain.org.mapper.OrgParticipantMemberMapper;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;
import com.ourhour.global.exception.BusinessException;
import com.ourhour.domain.member.dto.MemberOrgDetailResDTO;
import com.ourhour.domain.member.dto.MemberOrgSummaryResDTO;
import com.ourhour.domain.member.mapper.MemberOrgMapper;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.global.jwt.util.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ourhour.global.common.dto.PageResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberOrgMapper memberOrgMapper;
    private final OrgParticipantMemberRepository orgParticipantMemberRepository;
    private final OrgParticipantMemberMapper orgParticipantMemberMapper;

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

    // 회사 내 개인정보 상세 조회
    public MyMemberInfoResDTO findMyMemberInfoInOrg(Long orgId) {

        // 해당 회사 내에 내 정보가 있고 활성 상태인지 조회
        OrgParticipantMemberEntity opm = findMyMemberInOrgHelper(orgId);

        MyMemberInfoResDTO memberInfoResDTO = orgParticipantMemberMapper.toMyMemberInfoResDTO(opm);

        return memberInfoResDTO;

    }

    // 회사 내 개인정보 수정
    @Transactional
    public MyMemberInfoResDTO updateMyMemberInfoInOrg(Long orgId, MyMemberInfoReqDTO myInfoReqDTO) {

        // 해당 회사 내에 내 정보가 있고 활성 상태인지 조회
        OrgParticipantMemberEntity opm = findMyMemberInOrgHelper(orgId);
        MemberEntity memberEntity = opm.getMemberEntity();
        DepartmentEntity deptEntity = opm.getDepartmentEntity();
        PositionEntity positionEntity = opm.getPositionEntity();

        // memberEntity 업데이트
        memberEntity.changeMyMemberInfo(myInfoReqDTO.getName(), myInfoReqDTO.getPhone(), myInfoReqDTO.getEmail(), myInfoReqDTO.getProfileImgUrl());

        // 부서 재할당
        if (myInfoReqDTO.getDeptName() != null) {
        }

        // 직책 재할당
        if (myInfoReqDTO.getPositionName() != null) {

        }

        // Entity -> DTO 변환
        MyMemberInfoResDTO memberInfoResDTO = orgParticipantMemberMapper.toMyMemberInfoResDTO(opm);

        return memberInfoResDTO;

    }

    // 해당 회사 내에 내 정보가 있고 활성 상태인지 조회
    private OrgParticipantMemberEntity findMyMemberInOrgHelper(Long orgId) {

        return orgParticipantMemberRepository
                .findByOrgEntity_OrgIdAndMemberEntity_UserEntity_UserIdAndStatus(
                        orgId,
                        UserContextHolder.get().getUserId(),
                        Status.ACTIVE
                )
                .orElseThrow(MemberException::memberNotFoundException);
    }

}