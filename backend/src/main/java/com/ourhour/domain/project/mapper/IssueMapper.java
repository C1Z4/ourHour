package com.ourhour.domain.project.mapper;

import com.ourhour.domain.project.dto.IssueDetailDTO;
import com.ourhour.domain.project.dto.IssueSummaryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.ourhour.domain.project.entity.IssueEntity;
import com.ourhour.domain.project.dto.IssueReqDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IssueMapper {

    // IssueEntity -> IssueSummaryDTO
    @Mapping(target = "assigneeId", source = "assigneeEntity.memberId")
    @Mapping(target = "assigneeName", source = "assigneeEntity.name")
    @Mapping(target = "assigneeProfileImgUrl", source = "assigneeEntity.profileImgUrl")
    IssueSummaryDTO toIssueSummaryDTO(IssueEntity issueEntity);

    // IssueEntity -> IssueDetailDTO
    @Mapping(target = "assigneeId", source = "assigneeEntity.memberId")
    @Mapping(target = "assigneeName", source = "assigneeEntity.name")
    @Mapping(target = "assigneeProfileImgUrl", source = "assigneeEntity.profileImgUrl")
    @Mapping(target = "milestoneName", source = "milestoneEntity.name")
    IssueDetailDTO toIssueDetailDTO(IssueEntity issueEntity);

    // IssueReqDTO -> IssueEntity
    @Mapping(target = "milestoneEntity", ignore = true)
    @Mapping(target = "assigneeEntity", ignore = true)
    @Mapping(target = "projectEntity", ignore = true)
    IssueEntity toIssueEntity(IssueReqDTO issueReqDTO);
}
