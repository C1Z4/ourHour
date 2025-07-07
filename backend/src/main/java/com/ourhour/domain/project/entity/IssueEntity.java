package com.ourhour.domain.project.entity;

import com.ourhour.domain.project.enums.IssueStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_issue")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IssueEntity {

    @Id
    private Long issueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "milestone_id")
    private MilestoneEntity milestone;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_tag_id")
    private IssueTagEntity issueTag;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity assignee;


    private String name;
    private String content;
    private IssueStatus status;

}
