package com.ourhour.domain.org.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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

    @OneToMany(mappedBy = "orgEntity")
    private List<OrgParticipantMemberEntity> orgParticipantMemberEntityList = new ArrayList<>();

    private String name;
    private String address;
    private String email;
    private String representativeName;
    private String phone;
    private String businessNumber;
    private String logoImgUrl;

    @Builder
    public OrgEntity(String name, String address, String email, String representativeName, String phone, String businessNumber, String logoImgUrl) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.representativeName = representativeName;
        this.phone = phone;
        this.businessNumber = businessNumber;
        this.logoImgUrl = logoImgUrl;
    }


}
