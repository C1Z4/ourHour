package com.ourhour.domain.project.mapper;

import com.ourhour.domain.project.dto.IssueDetailDTO;
import com.ourhour.domain.project.dto.IssueSummaryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.ourhour.domain.project.entity.IssueEntity;
import com.ourhour.domain.project.dto.IssueReqDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IssueMapper {

    // IssueEntity -> IssueSummaryDTO
    @Mapping(target = "milestoneId", source = "milestoneEntity.milestoneId")
    @Mapping(target = "assigneeId", source = "assigneeEntity.memberId")
    @Mapping(target = "assigneeName", source = "assigneeEntity.name")
    @Mapping(target = "assigneeProfileImgUrl", source = "assigneeEntity.profileImgUrl")
    @Mapping(target = "status", expression = "java(issueEntity.getStatus().getDescription())")
    IssueSummaryDTO toIssueSummaryDTO(IssueEntity issueEntity);

    // IssueEntity -> IssueDetailDTO
    @Mapping(target = "assigneeId", source = "assigneeEntity.memberId")
    @Mapping(target = "assigneeName", source = "assigneeEntity.name")
    @Mapping(target = "assigneeProfileImgUrl", source = "assigneeEntity.profileImgUrl")
    @Mapping(target = "milestoneId", source = "milestoneEntity.milestoneId")
    @Mapping(target = "milestoneName", source = "milestoneEntity.name")
    @Mapping(target = "status", expression = "java(issueEntity.getStatus().getDescription())")
    IssueDetailDTO toIssueDetailDTO(IssueEntity issueEntity);

    // IssueReqDTO -> IssueEntity
    @Mapping(target = "issueId", ignore = true)
    @Mapping(target = "milestoneEntity", ignore = true)
    @Mapping(target = "assigneeEntity", ignore = true)
    @Mapping(target = "projectEntity", ignore = true)
    @Mapping(target = "issueTagEntity", ignore = true)
    IssueEntity toIssueEntity(IssueReqDTO issueReqDTO);

    // IssueEntity -> IssueEntity
    @Mapping(target = "milestoneEntity", ignore = true)
    @Mapping(target = "assigneeEntity", ignore = true)
    @Mapping(target = "projectEntity", ignore = true)
    @Mapping(target = "issueTagEntity", ignore = true)
    void updateIssueEntity(@MappingTarget IssueEntity issueEntity, IssueReqDTO issueReqDTO);
}
