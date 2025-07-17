package com.ourhour.domain.org.service;

import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.org.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ourhour.domain.org.exceptions.OrgException.notMuchRootAdminException;
import static com.ourhour.domain.org.exceptions.OrgException.tooMuchRootAdminException;

@Service
@RequiredArgsConstructor
public class OrgRoleGuardService {

    public void checkCanWithDraw(List<MemberEntity> memberEntityList) {

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
