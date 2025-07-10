package com.ourhour.domain.project.mapper;

import com.ourhour.domain.project.dto.ProjectParticipantDTO;
import com.ourhour.domain.project.entity.ProjectParticipantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectParticipantMapper {

    @Mapping(source = "projectParticipantId.orgParticipantMemberId.memberId", target = "memberId")
    @Mapping(source = "orgParticipantMemberEntity.memberEntity.name", target = "name")
    @Mapping(source = "orgParticipantMemberEntity.memberEntity.phone", target = "phone")
    @Mapping(source = "orgParticipantMemberEntity.memberEntity.email", target = "email")
    @Mapping(source = "orgParticipantMemberEntity.departmentEntity.name", target = "deptName")
    @Mapping(source = "orgParticipantMemberEntity.positionEntity.name", target = "positionName")
    @Mapping(source = "orgParticipantMemberEntity.memberEntity.profileImgUrl", target = "profileImgUrl")
    ProjectParticipantDTO toProjectParticipantDTO(ProjectParticipantEntity entity);

}