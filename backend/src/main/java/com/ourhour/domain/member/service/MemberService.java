package com.ourhour.domain.member.service;

import com.ourhour.domain.member.dto.MyMemberInfoReqDTO;
import com.ourhour.domain.member.dto.MyMemberInfoResDTO;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.exception.MemberException;
import com.ourhour.domain.org.entity.DepartmentEntity;
import com.ourhour.domain.org.entity.PositionEntity;
import com.ourhour.domain.org.enums.Status;
import com.ourhour.domain.org.mapper.OrgParticipantMemberMapper;
import com.ourhour.domain.org.repository.DepartmentRepository;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;
import com.ourhour.domain.org.repository.PositionRepository;
import com.ourhour.domain.member.dto.MemberOrgDetailResDTO;
import com.ourhour.domain.member.dto.MemberOrgSummaryResDTO;
import com.ourhour.domain.member.mapper.MemberOrgMapper;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.org.exception.OrgException;
import com.ourhour.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.ourhour.global.common.dto.PageResponse;
import com.ourhour.global.common.service.ImageService;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberOrgMapper memberOrgMapper;
    private final OrgParticipantMemberRepository orgParticipantMemberRepository;
    private final OrgParticipantMemberMapper orgParticipantMemberMapper;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final ImageService imageService;

    public MemberOrgSummaryResDTO findOrgSummaryByMemberId(Long memberId) {
        OrgParticipantMemberEntity orgParticipantMemberEntity = memberRepository.findOrgByMemberId(memberId);

        return memberOrgMapper.toMemberOrgSummaryResDTO(orgParticipantMemberEntity);
    }

    public PageResponse<MemberOrgSummaryResDTO> findOrgSummaryByMemberId(Long memberId, Pageable pageable) {

        Page<OrgParticipantMemberEntity> orgParticipantMemberEntityPage = memberRepository
                .findOrgListByMemberId(memberId, pageable);

        if (orgParticipantMemberEntityPage.isEmpty()) {
            return PageResponse.empty(pageable.getPageNumber() + 1, pageable.getPageSize());
        }

        Page<MemberOrgSummaryResDTO> dtoPage = orgParticipantMemberEntityPage
                .map(memberOrgMapper::toMemberOrgSummaryResDTO);

        return PageResponse.of(dtoPage);
    }

    // 참여 중인 회사 목록 조회 (userId 기반)
    public PageResponse<MemberOrgSummaryResDTO> findOrgSummaryByUserId(Long userId, Pageable pageable) {

        Page<OrgParticipantMemberEntity> orgParticipantMemberEntityPage = memberRepository
                .findOrgListByUserIdAndStatus(userId, Status.ACTIVE, pageable);

        if (orgParticipantMemberEntityPage.isEmpty()) {
            return PageResponse.empty(pageable.getPageNumber() + 1, pageable.getPageSize());
        }

        List<MemberOrgSummaryResDTO> dtoList = orgParticipantMemberEntityPage.getContent().stream()
                .map(memberOrgMapper::toMemberOrgSummaryResDTO)
                .toList();

        Page<MemberOrgSummaryResDTO> dtoPage = new PageImpl<>(dtoList, pageable, orgParticipantMemberEntityPage.getTotalElements());

        return PageResponse.of(dtoPage);
    }

    public MemberOrgDetailResDTO findOrgDetailByMemberIdAndOrgId(Long memberId, Long orgId) {

        // memberId와 orgId를 통해 본인이 속한 회사 상세 정보 조회
        OrgParticipantMemberEntity orgDetailEntity = memberRepository
                .findOrgDetailByMemberIdAndOrgId(memberId, orgId)
                .orElseThrow(() -> OrgException.orgNotFoundException());

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

        // 내 정보 조회
        MemberEntity memberEntity = opm.getMemberEntity();

        // 이미지 처리
        String profileImgUrl = myInfoReqDTO.getProfileImgUrl();

        // Base64 데이터인 경우 파일로 저장하고 URL로 변환
        if (profileImgUrl != null && profileImgUrl.startsWith("data:image/")) {
            profileImgUrl = imageService.saveBase64Image(profileImgUrl);
        }

        // 변환된 이미지 URL로 새로운 DTO 생성
        MyMemberInfoReqDTO updatedInfoReqDTO = myInfoReqDTO;
        if (profileImgUrl != null) {
            updatedInfoReqDTO = MyMemberInfoReqDTO.builder()
                    .name(myInfoReqDTO.getName())
                    .phone(myInfoReqDTO.getPhone())
                    .email(myInfoReqDTO.getEmail())
                    .profileImgUrl(profileImgUrl)
                    .deptId(myInfoReqDTO.getDeptId())
                    .positionId(myInfoReqDTO.getPositionId())
                    .build();
        }

        // memberEntity 업데이트
        memberEntity.changeMyMemberInfo(
            updatedInfoReqDTO.getName(),
            updatedInfoReqDTO.getPhone(),
            updatedInfoReqDTO.getEmail(),
            updatedInfoReqDTO.getProfileImgUrl()
        );

        // 부서 재할당
        if (updatedInfoReqDTO.getDeptId() != null) {
            DepartmentEntity newDepartment = departmentRepository.findById(updatedInfoReqDTO.getDeptId())
                    .orElseThrow(OrgException::departmentNotFoundException);
            opm.changeDepartment(newDepartment);
        } else {
            // 부서 없음으로 설정
            opm.changeDepartment(null);
        }

        // 직책 재할당  
        if (updatedInfoReqDTO.getPositionId() != null) {
            PositionEntity newPosition = positionRepository.findById(updatedInfoReqDTO.getPositionId())
                    .orElseThrow(OrgException::positionNotFoundException);
            opm.changePosition(newPosition);
        } else {
            // 직책 없음으로 설정
            opm.changePosition(null);
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
                        SecurityUtil.getCurrentUserId(),
                        Status.ACTIVE)
                .orElseThrow(MemberException::memberNotFoundException);
    }

}