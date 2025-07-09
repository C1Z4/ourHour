package com.ourhour.domain.member.mapper;

import com.ourhour.domain.member.dto.MemberOrgDetailResDTO;
import com.ourhour.domain.member.dto.MemberOrgSummaryResDTO;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MemberOrgMapper{

    // 단일 OrgParticipantMemberEntity를 MemberOrgListResDTO로 매핑하는 메소드
    @Mapping(source = "orgEntity.orgId", target = "orgId")
    @Mapping(source = "orgEntity.name", target = "name")
    @Mapping(source = "orgEntity.logoImgUrl", target="logoImgUrl")
    @Mapping(source = "departmentEntity.name", target="departmentName")
    @Mapping(source = "positionEntity.name", target="positionName")
    MemberOrgSummaryResDTO toMemberOrgSummaryResDTO(OrgParticipantMemberEntity entity);

    // OrgParticipantMemberEntity 리스트를 MemberOrgListResDTO 리스트로 매핑하는 메소드
    List<MemberOrgSummaryResDTO> toMemberOrgSummaryResDTOList(List<OrgParticipantMemberEntity> entityList);
    
    // OrgParticipantMemberEntity를 MemberOrgDetailResDTO로 매핑하는 메소드
    @Mapping(source = "orgEntity.orgId", target = "orgId")
    @Mapping(source = "orgEntity.name", target = "name")
    @Mapping(source = "orgEntity.address", target = "address")
    @Mapping(source = "orgEntity.email", target = "email")
    @Mapping(source = "orgEntity.representativeName", target = "representativeName")
    @Mapping(source = "orgEntity.phone", target = "phone")
    @Mapping(source = "orgEntity.businessNumber", target = "businessNumber")
    @Mapping(source = "orgEntity.logoImgUrl", target="logoImgUrl")
    @Mapping(source = "departmentEntity.name", target="departmentName")
    @Mapping(source = "positionEntity.name", target="positionName")
    MemberOrgDetailResDTO toMemberOrgDetailResDTO(OrgParticipantMemberEntity entity);

}