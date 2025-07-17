package com.ourhour.domain.org.mapper;

import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.org.dto.OrgMemberRoleResDTO;
import com.ourhour.domain.org.dto.OrgResDTO;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.org.enums.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrgParticipantMemberMapper {

    // DTO -> Entity 변환 (회사 등록)
    @Mapping(source = "orgEntity.orgId", target = "orgId")
    @Mapping(source = "orgEntity.name", target = "name")
    @Mapping(source = "orgEntity.address", target = "address")
    @Mapping(source = "orgEntity.email", target = "email")
    @Mapping(source = "orgEntity.representativeName", target = "representativeName")
    @Mapping(source = "orgEntity.phone", target = "phone")
    @Mapping(source = "orgEntity.businessNumber", target = "businessNumber")
    @Mapping(source = "orgEntity.logoImgUrl", target = "logoImgUrl")
    @Mapping(source = "memberEntity.name", target = "memberName")
    @Mapping(source = "orgParticipantMemberEntity.role", target = "myRole")
    OrgResDTO toOrgResDTO(OrgEntity orgEntity, MemberEntity memberEntity, OrgParticipantMemberEntity orgParticipantMemberEntity);

    // Entity -> DTO 변환 (구성원 권한 변경)
    @Mapping(source = "orgParticipantMemberEntity.orgEntity.orgId", target = "orgId")
    @Mapping(source = "orgParticipantMemberEntity.memberEntity.memberId", target = "memberId")
    @Mapping(source = "oldRole", target = "oldRole")
    @Mapping(source = "orgParticipantMemberEntity.role", target = "newRole")
    @Mapping(source = "rootAdminCount", target = "rootAdminCount")
    OrgMemberRoleResDTO toOrgMemberRoleResDTO(OrgParticipantMemberEntity orgParticipantMemberEntity, Role oldRole, int rootAdminCount);
}
