package com.ourhour.domain.member.entity;

import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    private String name;
    private String phone;
    private String email;
    private String profileImgUrl;

    @OneToOne(mappedBy = "memberEntity", fetch = FetchType.LAZY)
    private OrgParticipantMemberEntity orgParticipantMemberEntity;
}