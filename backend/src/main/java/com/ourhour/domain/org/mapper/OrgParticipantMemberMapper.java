package com.ourhour.domain.org.mapper;

import com.ourhour.domain.member.dto.MyMemberInfoResDTO;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.dto.MemberInfoResDTO;
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
        @Mapping(target = "myRole", expression = "java(orgParticipantMemberEntity.getRole().getDescription())")
        OrgResDTO toOrgResDTO(OrgEntity orgEntity, MemberEntity memberEntity,
                        OrgParticipantMemberEntity orgParticipantMemberEntity);

        // Entity -> DTO 변환 (구성원 권한 변경)
        @Mapping(source = "orgParticipantMemberEntity.orgEntity.orgId", target = "orgId")
        @Mapping(source = "orgParticipantMemberEntity.memberEntity.memberId", target = "memberId")
        @Mapping(target = "oldRole", expression = "java(oldRole.getDescription())")
        @Mapping(target = "newRole", expression = "java(orgParticipantMemberEntity.getRole().getDescription())")
        @Mapping(source = "rootAdminCount", target = "rootAdminCount")
        OrgMemberRoleResDTO toOrgMemberRoleResDTO(OrgParticipantMemberEntity orgParticipantMemberEntity, Role oldRole,
                        int rootAdminCount);

        // Entity -> DTO 변환 (구성원 상세 조회)
        @Mapping(source = "orgParticipantMemberEntity.memberEntity.memberId", target = "memberId")
        @Mapping(source = "orgParticipantMemberEntity.memberEntity.name", target = "name")
        @Mapping(source = "orgParticipantMemberEntity.memberEntity.email", target = "email")
        @Mapping(source = "orgParticipantMemberEntity.memberEntity.phone", target = "phone")
        @Mapping(source = "orgParticipantMemberEntity.positionEntity.name", target = "positionName")
        @Mapping(source = "orgParticipantMemberEntity.departmentEntity.name", target = "deptName")
        @Mapping(source = "orgParticipantMemberEntity.memberEntity.profileImgUrl", target = "profileImgUrl")
        @Mapping(target = "role", expression = "java(orgParticipantMemberEntity.getRole().getDescription())")
        MemberInfoResDTO toMemberInfoResDTO(OrgParticipantMemberEntity orgParticipantMemberEntity);

        // Enity -> DTO 변화 (회사 내 개인정보 조회)
        @Mapping(source = "opm.memberEntity.memberId", target = "memberId")
        @Mapping(source = "opm.orgEntity.orgId", target = "orgId")
        @Mapping(source = "opm.memberEntity.name", target = "name")
        @Mapping(source = "opm.memberEntity.phone", target = "phone")
        @Mapping(source = "opm.memberEntity.email", target = "email")
        @Mapping(source = "opm.memberEntity.profileImgUrl", target = "profileImgUrl")
        @Mapping(source = "opm.departmentEntity.deptId", target = "deptId")
        @Mapping(source = "opm.positionEntity.positionId", target = "positionId")
        @Mapping(source = "opm.departmentEntity.name", target = "deptName")
        @Mapping(source = "opm.positionEntity.name", target = "positionName")
        @Mapping(target = "role", expression = "java(opm.getRole().getDescription())")
        @Mapping(target = "isGithubLinked", expression = "java(opm.getMemberEntity().getUserEntity().getGithubMappingEntity() != null)")
        MyMemberInfoResDTO toMyMemberInfoResDTO(OrgParticipantMemberEntity opm);

}
