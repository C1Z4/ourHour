package com.ourhour.domain.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.ourhour.domain.project.dto.IssueTagDTO;
import com.ourhour.domain.project.entity.IssueTagEntity;

@Mapper(componentModel = "spring")
public interface IssueTagMapper {
    IssueTagDTO toIssueTagDTO(IssueTagEntity issueTagEntity);

    IssueTagEntity toIssueTagEntity(IssueTagDTO issueTagDTO);

    @Mapping(target = "projectEntity", ignore = true)
    @Mapping(target = "issueTagId", ignore = true)
    void updateIssueTagEntity(@MappingTarget IssueTagEntity issueTagEntity, IssueTagDTO issueTagDTO);
}
