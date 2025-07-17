package com.ourhour.domain.org.service;

import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.org.enums.Status;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.ourhour.domain.org.exceptions.OrgException.*;

@Service
@RequiredArgsConstructor
public class OrgRoleGuardService {

    private final OrgParticipantMemberRepository orgParticipantMemberRepository;

    // 계정 탈퇴 예외 처리
    public void assertNotLastRootAdminAcrossAll(List<MemberEntity> memberEntityList) {

        // 소속 조직이 없으면 바로 탈퇴 가능
        if (memberEntityList == null || memberEntityList.isEmpty()) {
            return;
        }

        List<String> blockedOrgList = new ArrayList<>();

        // 해당 멤버가 소속되어 있는 모든 회사 참여 정보 조회 (ACTIVE 상태인 경우만)
        for (MemberEntity member : memberEntityList) {
            List<OrgParticipantMemberEntity> orgParticipantMemberEntityList
                    = orgParticipantMemberRepository.findAllByMemberEntity_MemberIdAndStatus(member.getMemberId(), Status.ACTIVE);

            // 루트 관리자가 하나인 회사가 하나라도 존재하는 경우 계정 탈퇴 불가
            for (OrgParticipantMemberEntity opm : orgParticipantMemberEntityList) {
                if (opm.getRole() != Role.ROOT_ADMIN) continue;

                // 루트 관리자인 경우 해당 회사의 루트 관리자 수 조회
                Long orgId = opm.getOrgEntity().getOrgId();
                int rootAdminCount = orgParticipantMemberRepository.countRootAdmins(orgId);

                if (rootAdminCount <= 1) {
                    String orgName = opm.getOrgEntity().getName();
                    blockedOrgList.add(orgName);
                }
            }
        }

        if (!blockedOrgList.isEmpty()) {
            throw deleteUserException(String.join(", ", blockedOrgList));
        }

    }

    public void assertRoleChangeAllowed (Role oldRole, Role newRole, int rootAdminCount) {

        // 루트 관리자-> 다른 권한 : 루트 관리자 최소 정책 위반(최소 1명)
        if (oldRole == Role.ROOT_ADMIN && newRole != Role.ROOT_ADMIN) {
            // 현재 루트 관리자 수가 1이하일 때 루트 관리자 권한 해제 시 예외
            if (rootAdminCount <= 1) {
                throw notMuchRootAdminException();
            }
        }

        // 다른 권한 -> 루트 관리자 : 루트 관리자 최대 정책 위반(최대 2명)
        if (oldRole != Role.ROOT_ADMIN && newRole == Role.ROOT_ADMIN) {
            // 현재 루트 관리자 수가 2일 때 루트 관리자 권한 부여 시 예외
            if (rootAdminCount >= 2) {
                throw tooMuchRootAdminException();
            }
        }

    }
}
