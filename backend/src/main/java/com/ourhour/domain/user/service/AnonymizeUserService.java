package com.ourhour.domain.user.service;

import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnonymizeUserService {

    // 탈퇴한 계정 익명화
    public void anonymizeUser(List<MemberEntity> memberEntityList) {

        // 탈퇴한 계정의 userId를 가지고 있는 멤버 엔티티의 이름 익명화하기
        for (MemberEntity memberEntity : memberEntityList) {
            String suffix = UUID.randomUUID().toString().substring(0,8);
            memberEntity.anonymizeName(suffix);
        }

    }

    // 삭제된 또는

}
