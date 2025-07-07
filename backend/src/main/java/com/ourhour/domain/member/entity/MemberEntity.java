package com.ourhour.domain.member.entity;

import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "tbl_member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private UserEntity userEntity;

    private String name;
    private String phone;
    private String email;
    private String profileImg;
}
