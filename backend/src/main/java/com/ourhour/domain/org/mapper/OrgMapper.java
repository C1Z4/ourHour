package com.ourhour.domain.org.mapper;

import com.ourhour.domain.org.dto.OrgReqDTO;
import com.ourhour.domain.org.dto.OrgResDTO;
import com.ourhour.domain.org.entity.OrgEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrgMapper {

    // ReqDTO -> Entity 변환 (DB 등록)
    OrgEntity toOrgEntity(OrgReqDTO orgReqDTO);

    // Entity -> ResDTO 변환 (응답)
    OrgResDTO toOrgResDTO(OrgEntity orgEntity);
}
