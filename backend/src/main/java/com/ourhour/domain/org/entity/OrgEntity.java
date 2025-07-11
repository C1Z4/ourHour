package com.ourhour.domain.org.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_org")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrgEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orgId;

    @OneToMany(mappedBy = "orgEntity")
    private List<OrgParticipantMemberEntity> orgParticipantMemberEntityList = new ArrayList<>();

    @Setter
    private String name;
    @Setter
    private String address;
    @Setter
    private String email;
    @Setter
    private String representativeName;
    @Setter
    private String phone;
    @Setter
    private String businessNumber;
    @Setter
    private String logoImgUrl;

    @Builder
    public OrgEntity(String name, String address, String email, String representativeName, String phone,
            String businessNumber, String logoImgUrl) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.representativeName = representativeName;
        this.phone = phone;
        this.businessNumber = businessNumber;
        this.logoImgUrl = logoImgUrl;
    }

}
