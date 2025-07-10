package com.ourhour.domain.project.mapper;

import com.ourhour.domain.project.dto.ProjectInfoDTO;
import com.ourhour.domain.project.dto.ProjectSummaryResDTO;
import com.ourhour.domain.project.entity.ProjectEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    // ProjectEntity -> ProjectSummaryResDTO
    @Mapping(target = "participants", ignore = true)
    ProjectSummaryResDTO toProjectSummaryResDTO(ProjectEntity entity);

    // ProjectEntity -> ProjectInfoDTO
    ProjectInfoDTO toProjectInfoDTO(ProjectEntity entity);

}