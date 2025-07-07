package com.ourhour.domain.project.entity;

import com.ourhour.domain.project.enums.IssueTagColor;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_issue_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IssueTagEntity {

    @Id
    private Long issueTagId;
    private String name;
    private IssueTagColor color;
}
