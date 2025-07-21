package com.ourhour.domain.org.mapper;


import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.org.dto.OrgResDTO;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrgRegistrationMapper {
    @Mapping(source = "orgEntity.orgId", target = "orgId")
    @Mapping(source = "orgEntity.name", target = "name")
    @Mapping(source = "orgEntity.address", target = "address")
    @Mapping(source = "orgEntity.email", target = "email")
    @Mapping(source = "orgEntity.representativeName", target = "representativeName")
    @Mapping(source = "orgEntity.phone", target = "phone")
    @Mapping(source = "orgEntity.businessNumber", target = "businessNumber")
    @Mapping(source = "orgEntity.logoImgUrl", target = "logoImgUrl")
    @Mapping(source = "memberEntity.name", target = "memberName")
    @Mapping(target = "myRole", expression = "java(orgParticipantMemberEntity.getRole().getDescription())")
    OrgResDTO toOrgResDTO(OrgEntity orgEntity, MemberEntity memberEntity, OrgParticipantMemberEntity orgParticipantMemberEntity);


}
