package com.ourhour.domain.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.ourhour.domain.project.dto.MileStoneInfoDTO;
import com.ourhour.domain.project.entity.MilestoneEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MilestoneMapper {

    // MilestoneEntity -> MileStoneInfoDTO
    MileStoneInfoDTO toMileStoneInfoDTO(MilestoneEntity entity);    

}
