package com.ourhour.domain.project.mapper;

import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.project.dto.ProjectUpdateReqDTO;
import com.ourhour.domain.project.dto.ProjectInfoDTO;
import com.ourhour.domain.project.dto.ProjectReqDTO;
import com.ourhour.domain.project.dto.ProjectSummaryResDTO;
import com.ourhour.domain.project.entity.ProjectEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    // ProjectEntity -> ProjectSummaryResDTO
    @Mapping(target = "status", expression = "java(entity.getStatus().getDescription())")
    @Mapping(target = "participants", ignore = true)
    ProjectSummaryResDTO toProjectSummaryResDTO(ProjectEntity entity);

    // ProjectEntity -> ProjectInfoDTO
    @Mapping(target = "status", expression = "java(entity.getStatus().getDescription())")
    ProjectInfoDTO toProjectInfoDTO(ProjectEntity entity);

    // ProjectReqDTO -> ProjectEntity
    @Mapping(target = "orgEntity", source = "orgEntity")
    @Mapping(target = "name", source = "projectReqDTO.name")
    ProjectEntity toProjectEntity(OrgEntity orgEntity, ProjectReqDTO projectReqDTO);

    // ProjectUpdateReqDTO -> ProjectEntity
    @Mapping(target = "projectId", ignore = true)
    void updateProjectEntity(@MappingTarget ProjectEntity projectEntity, ProjectUpdateReqDTO projectUpdateReqDTO);
}