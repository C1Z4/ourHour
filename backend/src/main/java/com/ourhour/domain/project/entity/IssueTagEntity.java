package com.ourhour.domain.project.entity;

import com.ourhour.global.common.enums.TagColor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_issue_tag")
@Getter
@NoArgsConstructor
public class IssueTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_tag_id")
    private Long issueTagId;

    @Setter
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity projectEntity;

    @Setter
    private String name;

    @Setter
    @Enumerated(EnumType.STRING)
    private TagColor color;
}
