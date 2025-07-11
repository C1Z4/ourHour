package com.ourhour.domain.project.mapper;

import com.ourhour.domain.project.dto.IssueDetailDTO;
import com.ourhour.domain.project.dto.IssueSummaryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.ourhour.domain.project.entity.IssueEntity;

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
    IssueDetailDTO toIssueDetailDTO(IssueEntity issueEntity);
}
