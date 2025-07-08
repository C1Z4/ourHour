package com.ourhour.domain.project.entity;

import com.ourhour.global.common.enums.TagColor;
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
    private TagColor color;
}
