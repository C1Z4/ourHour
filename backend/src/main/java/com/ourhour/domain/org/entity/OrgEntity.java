package com.ourhour.domain.org.entity;

import com.ourhour.domain.org.dto.OrgDetailReqDTO;
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

    // 회사 등록
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

    // 회사 정보 수정
    public void updateInfo(OrgDetailReqDTO orgDetailReqDTO) {
        if (orgDetailReqDTO.getName() != null) this.name = orgDetailReqDTO.getName();
        if (orgDetailReqDTO.getAddress() != null) this.address = orgDetailReqDTO.getAddress();
        if (orgDetailReqDTO.getEmail() != null) this.email = orgDetailReqDTO.getEmail();
        if (orgDetailReqDTO.getRepresentativeName() != null) this.representativeName = orgDetailReqDTO.getRepresentativeName();
        if (orgDetailReqDTO.getPhone() != null) this.phone = orgDetailReqDTO.getPhone();
        if (orgDetailReqDTO.getBusinessNumber() != null) this.businessNumber = orgDetailReqDTO.getBusinessNumber();
        if (orgDetailReqDTO.getLogoImgUrl() != null) this.logoImgUrl = orgDetailReqDTO.getLogoImgUrl();
    }

}
