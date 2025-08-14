package com.ourhour.domain.org.mapper;

import com.ourhour.domain.org.dto.OrgInvResDTO;
import com.ourhour.domain.org.entity.OrgInvEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrgInvMapper {

    @Mapping(target = "email", source = "email")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "status", source = "status")
    OrgInvResDTO toOrgInvResDTO(OrgInvEntity orgInvEntity);
}
