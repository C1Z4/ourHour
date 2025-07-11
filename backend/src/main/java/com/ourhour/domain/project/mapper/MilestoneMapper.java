package com.ourhour.domain.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import com.ourhour.domain.project.dto.MileStoneInfoDTO;
import com.ourhour.domain.project.entity.MilestoneEntity;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.ourhour.domain.project.dto.MileStoneInfoDTO;
import com.ourhour.domain.project.dto.MilestoneReqDTO;
import com.ourhour.domain.project.entity.MilestoneEntity;
import com.ourhour.domain.project.entity.ProjectEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MilestoneMapper {

    // MilestoneEntity -> MileStoneInfoDTO
    MileStoneInfoDTO toMileStoneInfoDTO(MilestoneEntity entity);

    // MilestoneReqDTO -> MilestoneEntity
    @Mapping(target = "name", source = "milestoneReqDTO.name")
    MilestoneEntity toMilestoneEntity(ProjectEntity projectEntity, MilestoneReqDTO milestoneReqDTO);

    // MilestoneReqDTO -> MilestoneEntity
    @Mapping(target = "milestoneId", ignore = true)
    @Mapping(target = "projectEntity", ignore = true)
    void updateMilestoneEntity(@MappingTarget MilestoneEntity milestoneEntity, MilestoneReqDTO milestoneReqDTO);

}
