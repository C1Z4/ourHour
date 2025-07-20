package com.ourhour.domain.user.service;

import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.org.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnonymizeUserService {

    private final MemberRepository memberRepository;

    // 탈퇴한 계정 익명화
    public void anonymizeUser(List<MemberEntity> memberEntityList) {

        // 탈퇴한 계정의 userId를 가지고 있는 멤버 엔티티의 이름 익명화하기
        for (MemberEntity memberEntity : memberEntityList) {
            anonymizeHelper(memberEntity);
        }

    }

    // 삭제된 또는 회사 나간 멤버 익명화
    public void anonymizeMember(OrgParticipantMemberEntity opm) {

        // 해당 회사 멤버 엔티티 조회
        MemberEntity memberEntity = opm.getMemberEntity();

        if (opm.getStatus() == Status.INACTIVE) {
            anonymizeHelper(memberEntity);
        }

    }

    // 멤버 익명화 공통 처리
    private void anonymizeHelper(MemberEntity memberEntity) {

        String suffix = UUID.randomUUID().toString().substring(0,4);
        memberEntity.anonymizeMember(suffix);

    }

}
