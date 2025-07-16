package com.ourhour.domain.org.service;

import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.org.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.ourhour.domain.user.exception.UserException.roleConflict;

@Service
@RequiredArgsConstructor
public class OrgRoleGuardService {

    public void checkCanWithDraw(List<MemberEntity> memberEntityList) {

        // 해당 멤버가 속해 있는 모든 회사 탐색(MemberEntity에 연결된 모든 OrgParticipantMemberEntity 조회)
        List<OrgParticipantMemberEntity> orgParticipantMemberEntityList = new ArrayList<>();
        for (MemberEntity memberEntity : memberEntityList) {

            List<OrgParticipantMemberEntity> participantList = memberEntity.getOrgParticipantMemberEntityList();
            orgParticipantMemberEntityList.addAll(participantList);

        }

        // 해당 회사에 대한 권한 확인
        for (OrgParticipantMemberEntity orgParticipantMemberEntity : orgParticipantMemberEntityList) {
            Role role = orgParticipantMemberEntity.getRole();
            if (role.equals(Role.ROOT_ADMIN)) {
                throw roleConflict();
            }
        }

    }
}
