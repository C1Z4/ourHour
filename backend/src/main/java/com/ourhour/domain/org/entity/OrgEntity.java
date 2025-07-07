package com.ourhour.domain.org.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_org")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrgEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orgId;

    @OneToMany(mappedBy = "orgParticipantMemberId.orgEntity")
    private List<OrgParticipantMemberEntity> orgParticipantMemberEntityList = new ArrayList<>();

    private String name;
    private String address;
    private String email;
    private String representativeName;
    private String phone;
    private String businessNumber;
    private String logoImgUrl;

}
