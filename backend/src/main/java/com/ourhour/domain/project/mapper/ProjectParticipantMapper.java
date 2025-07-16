package com.ourhour.domain.project.mapper;

import com.ourhour.domain.project.dto.ProjectParticipantDTO;
import com.ourhour.domain.project.entity.ProjectParticipantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectParticipantMapper {

    @Mapping(source = "entity.projectParticipantId.memberId", target = "memberId")
    @Mapping(source = "entity.memberEntity.name", target = "name")
    @Mapping(source = "entity.memberEntity.phone", target = "phone")
    @Mapping(source = "entity.memberEntity.email", target = "email")
    @Mapping(source = "entity.memberEntity.profileImgUrl", target = "profileImgUrl")
    @Mapping(target = "deptName", expression = "java(entity.getDeptName())")
    @Mapping(target = "positionName", expression = "java(entity.getPositionName())")
    ProjectParticipantDTO toProjectParticipantDTO(ProjectParticipantEntity entity, Long orgId);

}