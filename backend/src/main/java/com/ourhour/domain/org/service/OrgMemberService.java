package com.ourhour.domain.org.service;

import com.ourhour.domain.member.dto.MemberInfoResDTO;
import com.ourhour.domain.member.exception.MemberException;
import com.ourhour.domain.org.dto.OrgMemberRoleReqDTO;
import com.ourhour.domain.org.dto.OrgMemberRoleResDTO;
import com.ourhour.domain.org.entity.DepartmentEntity;
import com.ourhour.domain.org.entity.PositionEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.org.enums.Status;
import com.ourhour.domain.org.exception.OrgException;
import com.ourhour.domain.org.mapper.OrgParticipantMemberMapper;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;
import com.ourhour.domain.org.repository.OrgRepository;
import com.ourhour.domain.user.service.AnonymizeUserService;
import com.ourhour.global.common.dto.PageResponse;
import com.ourhour.global.jwt.util.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrgMemberService {

    private final OrgRepository orgRepository;
    private final OrgParticipantMemberRepository orgParticipantMemberRepository;
    private final OrgParticipantMemberMapper orgParticipantMemberMapper;
    private final OrgRoleGuardService orgRoleGuardService;
    private final AnonymizeUserService anonymizeUserService;

    // 회사 구성원 목록 조회
    public PageResponse<MemberInfoResDTO> getOrgMembers(Long orgId, String search, Pageable pageable) {

        if (orgId == null || orgId <= 0) {
            throw OrgException.orgNotFoundException();
        }

        // 회사 존재 여부 확인
        if (!orgRepository.existsById(orgId)) {
            throw OrgException.orgNotFoundException();
        }

        Page<OrgParticipantMemberEntity> page;
        if (search != null && !search.trim().isEmpty()) {
            page = orgParticipantMemberRepository.findByOrgIdAndNameContaining(orgId, search.trim(), pageable);
        } else {
            page = orgParticipantMemberRepository.findByOrgId(orgId, pageable);
        }

        Page<MemberInfoResDTO> memberInfoPage = page.map(entity -> {
            MemberInfoResDTO dto = new MemberInfoResDTO();
            dto.setMemberId(entity.getMemberEntity().getMemberId());
            dto.setName(entity.getMemberEntity().getName());
            dto.setEmail(entity.getMemberEntity().getEmail());
            dto.setPhone(entity.getMemberEntity().getPhone());
            dto.setPositionName(
                    Optional.ofNullable(entity.getPositionEntity()).map(PositionEntity::getName).orElse(null));
            dto.setDeptName(
                    Optional.ofNullable(entity.getDepartmentEntity()).map(DepartmentEntity::getName).orElse(null));
            dto.setProfileImgUrl(entity.getMemberEntity().getProfileImgUrl());
            dto.setRole(entity.getRole().getDescription());
            return dto;
        });

        return PageResponse.of(memberInfoPage);
    }

    // 회사 구성원 상세 조회
    public MemberInfoResDTO getOrgMember(Long orgId, Long memberId) {

        OrgParticipantMemberEntity orgParticipantMemberEntity = orgParticipantMemberRepository
                .findByOrgEntity_OrgIdAndMemberEntity_MemberId(orgId, memberId);

        MemberInfoResDTO memberInfoResDTO = orgParticipantMemberMapper.toMemberInfoResDTO(orgParticipantMemberEntity);

        return memberInfoResDTO;

    }

    // 특정 부서의 구성원 목록 조회
    public List<MemberInfoResDTO> getMembersByDepartment(Long orgId, Long deptId) {
        if (orgId == null || orgId <= 0) {
            throw OrgException.orgNotFoundException();
        }

        if (deptId == null || deptId <= 0) {
            throw OrgException.departmentNotFoundException();
        }

        // 회사 존재 여부 확인
        if (!orgRepository.existsById(orgId)) {
            throw OrgException.orgNotFoundException();
        }

        List<OrgParticipantMemberEntity> members = orgParticipantMemberRepository.findByOrgIdAndDeptId(orgId, deptId);

        return members.stream()
                .map(entity -> {
                    MemberInfoResDTO dto = new MemberInfoResDTO();
                    dto.setMemberId(entity.getMemberEntity().getMemberId());
                    dto.setName(entity.getMemberEntity().getName());
                    dto.setEmail(entity.getMemberEntity().getEmail());
                    dto.setPhone(entity.getMemberEntity().getPhone());
                    dto.setPositionName(
                            Optional.ofNullable(entity.getPositionEntity()).map(PositionEntity::getName).orElse(null));
                    dto.setDeptName(
                            Optional.ofNullable(entity.getDepartmentEntity()).map(DepartmentEntity::getName)
                                    .orElse(null));
                    dto.setProfileImgUrl(entity.getMemberEntity().getProfileImgUrl());
                    dto.setRole(entity.getRole().getDescription());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 특정 직책의 구성원 목록 조회
    public List<MemberInfoResDTO> getMembersByPosition(Long orgId, Long positionId) {
        if (orgId == null || orgId <= 0) {
            throw OrgException.orgNotFoundException();
        }

        if (positionId == null || positionId <= 0) {
            throw OrgException.positionNotFoundException();
        }

        // 회사 존재 여부 확인
        if (!orgRepository.existsById(orgId)) {
            throw OrgException.orgNotFoundException();
        }

        List<OrgParticipantMemberEntity> members = orgParticipantMemberRepository.findByOrgIdAndPositionId(orgId,
                positionId);

        return members.stream()
                .map(entity -> {
                    MemberInfoResDTO dto = new MemberInfoResDTO();
                    dto.setMemberId(entity.getMemberEntity().getMemberId());
                    dto.setName(entity.getMemberEntity().getName());
                    dto.setEmail(entity.getMemberEntity().getEmail());
                    dto.setPhone(entity.getMemberEntity().getPhone());
                    dto.setPositionName(
                            Optional.ofNullable(entity.getPositionEntity()).map(PositionEntity::getName).orElse(null));
                    dto.setDeptName(
                            Optional.ofNullable(entity.getDepartmentEntity()).map(DepartmentEntity::getName)
                                    .orElse(null));
                    dto.setProfileImgUrl(entity.getMemberEntity().getProfileImgUrl());
                    dto.setRole(entity.getRole().getDescription());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 구성원 권한 변경
    @Transactional
    public OrgMemberRoleResDTO changeRole(Long orgId, Long memberId, OrgMemberRoleReqDTO orgMemberRoleReqDTO) {

        // 활성화 상태인 대상 멤버 조회
        OrgParticipantMemberEntity orgParticipantMemberEntity = orgParticipantMemberRepository
                .findByOrgEntity_OrgIdAndMemberEntity_MemberIdAndStatus(orgId, memberId, Status.ACTIVE)
                .orElseThrow(MemberException::memberNotFoundException);

        Role oldRole = orgParticipantMemberEntity.getRole();
        Role newRole = orgMemberRoleReqDTO.getNewRole();
        int currentRootAdminCount = orgParticipantMemberRepository.countRootAdmins(orgId);

        // 동일 권한 변경일 경우 early return(정책 위반 아님)
        if (oldRole == newRole) {
            return orgParticipantMemberMapper.toOrgMemberRoleResDTO(orgParticipantMemberEntity, oldRole,
                    currentRootAdminCount);
        }

        // 루트 관리자 정책 검사
        orgRoleGuardService.assertRoleChangeAllowed(oldRole, newRole, currentRootAdminCount);

        // 구성원 권한 변경
        orgParticipantMemberEntity.changeRole(newRole);

        int afterRootAdminCount = currentRootAdminCount
                + (oldRole == Role.ROOT_ADMIN ? -1 : 0)
                + (newRole == Role.ROOT_ADMIN ? 1 : 0);

        // 엔티티 -> DTO 변환
        OrgMemberRoleResDTO orgMemberRoleResDTO = orgParticipantMemberMapper
                .toOrgMemberRoleResDTO(orgParticipantMemberEntity, oldRole, afterRootAdminCount);

        return orgMemberRoleResDTO;

    }

    // 구성원 삭제
    // TODO : 리스트 삭제
    @Transactional
    public void deleteOrgMember(Long orgId, Long memberId) {

        // 활성화 상태인 삭제대상 멤버 조회
        OrgParticipantMemberEntity opm = orgParticipantMemberRepository
                .findByOrgEntity_OrgIdAndMemberEntity_MemberIdAndStatus(orgId, memberId, Status.ACTIVE)
                .orElseThrow(MemberException::memberNotFoundException);

        // 마지막 루트 관리자 정책 검토
        orgRoleGuardService.assertNotLastRootAdminInOrg(orgId, memberId);

        // 삭제 처리
        opm.changeStatus(Status.INACTIVE);

        // 삭제 일자 업데이트
        opm.markLeftNow();

        // 삭제된 사용자 익명 처리
        anonymizeUserService.anonymizeMember(opm);

    }

    // 회사 나가기
    // TODO: 비밀번호 확인 API와 연동하여 삭제 요청 전에 재인증 로직 추가 필요
    @Transactional
    public void exitOrg(Long orgId) {

        // 현재 사용자 ID
        Long userId = UserContextHolder.get().getUserId();

        // 해당 회사에 속한 멤버 엔티티 찾기
        OrgParticipantMemberEntity opm = orgParticipantMemberRepository
                .findByOrgEntity_OrgIdAndMemberEntity_UserEntity_UserIdAndStatus(orgId, userId, Status.ACTIVE)
                .orElseThrow(() -> OrgException.orgMemberNotFoundException());

        // 루트 관리자 정책 확인
        orgRoleGuardService.assertNotLastRootAdminInOrg(opm);

        // 해당 회사의 멤버 INACTIVE 처리
        opm.changeStatus(Status.INACTIVE);

        // 나간 일자 업데이트
        opm.markLeftNow();

        // 나간 사용자 익명 처리
        anonymizeUserService.anonymizeMember(opm);

    }

    public List<MemberInfoResDTO> getAllOrgMembers(Long orgId) {

        if (orgId == null || orgId <= 0) {
            throw OrgException.orgNotFoundException();
        }

        // 회사 존재 여부 확인
        if (!orgRepository.existsById(orgId)) {
            throw OrgException.orgNotFoundException();
        }

        List<OrgParticipantMemberEntity> orgParticipantMemberEntityList = orgParticipantMemberRepository
                .findAllByOrgEntity_OrgId(orgId);
        List<MemberInfoResDTO> memberInfoResDTOList = orgParticipantMemberEntityList.stream()
                .map(orgParticipantMemberMapper::toMemberInfoResDTO)
                .collect(Collectors.toList());

        return memberInfoResDTOList;
    }

}
