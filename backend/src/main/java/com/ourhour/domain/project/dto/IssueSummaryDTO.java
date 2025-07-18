package com.ourhour.domain.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class IssueSummaryDTO {

    private Long issueId;
    private String name;
    private String status;
    private Long milestoneId;
    private Long assigneeId;
    private String assigneeName;
    private String assigneeProfileImgUrl;

}