package com.ourhour.domain.org.mapper;

import com.ourhour.domain.org.dto.OrgReqDTO;
import com.ourhour.domain.org.dto.OrgDetailReqDTO;
import com.ourhour.domain.org.dto.OrgDetailResDTO;
import com.ourhour.domain.org.entity.OrgEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrgMapper {

    // OrgReqDTO -> entity 변환
    OrgEntity toOrgEntity(OrgReqDTO orgReqDTO);


    // Entity -> ResDTO 변환 (응답)
    OrgDetailResDTO toOrgDetailResDTO(OrgEntity orgEntity);
    
}
