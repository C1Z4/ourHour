package com.ourhour.domain.project.mapper;

import com.ourhour.domain.project.dto.ProjectParticipantDTO;
import com.ourhour.domain.project.entity.ProjectParticipantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectParticipantMapper {

    @Mapping(source = "projectParticipantId.memberId", target = "memberId")
    @Mapping(source = "memberEntity.name", target = "name")
    @Mapping(source = "memberEntity.phone", target = "phone")
    @Mapping(source = "memberEntity.email", target = "email")
    @Mapping(source = "memberEntity.orgParticipantMemberEntity.departmentEntity.name", target = "deptName")
    @Mapping(source = "memberEntity.orgParticipantMemberEntity.positionEntity.name", target = "positionName")
    @Mapping(source = "memberEntity.profileImgUrl", target = "profileImgUrl")
    ProjectParticipantDTO toProjectParticipantDTO(ProjectParticipantEntity entity);

}