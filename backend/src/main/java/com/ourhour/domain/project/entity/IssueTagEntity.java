package com.ourhour.domain.project.entity;

import com.ourhour.domain.project.enums.IssueTagColor;
import jakarta.persistence.*;
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

    @Enumerated(EnumType.STRING)
    private IssueTagColor color;
}
