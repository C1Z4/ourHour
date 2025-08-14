package com.ourhour.domain.member.entity;

import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "tbl_member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

    @OneToMany(mappedBy = "memberEntity", fetch = FetchType.LAZY)
    private List<OrgParticipantMemberEntity> orgParticipantMemberEntityList;

    @Builder
    public MemberEntity(UserEntity userEntity, String name, String email) {
        this.userEntity = userEntity;
        this.name = name;
        this.email = email;
    }

    public void anonymizeMember(String suffix) {
        this.name = "Anonymous_Name" + suffix;
        this.email = "Anonymous_Email" + suffix;
    }
    
    public void changeMyMemberInfo(String name, String phone, String email, String profileImgUrl) {
        if (name != null) this.name = name;
        if (phone != null) this.phone = phone;
        if (email != null) this.email = email;
        if (profileImgUrl != null) this.profileImgUrl = profileImgUrl;
    }
}