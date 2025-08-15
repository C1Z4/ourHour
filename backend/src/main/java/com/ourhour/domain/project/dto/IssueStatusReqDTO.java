package com.ourhour.domain.project.dto;

import com.ourhour.domain.project.enums.IssueStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class IssueStatusReqDTO {

    private IssueStatus status;

}
