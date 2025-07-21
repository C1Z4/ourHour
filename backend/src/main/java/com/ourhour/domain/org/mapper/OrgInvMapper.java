package com.ourhour.domain.org.mapper;

import com.ourhour.domain.org.dto.OrgInvResDTO;
import com.ourhour.domain.org.entity.OrgInvEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrgInvMapper {

    // Entity -> DTO 변환
    OrgInvResDTO toOrgInvResDTO(OrgInvEntity orgInvEntity);
}
