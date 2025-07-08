package com.ourhour.domain.project.entity;

import com.ourhour.domain.member.entity.MemberEntity;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long issueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "milestone_id")
    private MilestoneEntity milestoneEntity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_tag_id")
    private IssueTagEntity issueTagEntity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private MemberEntity assigneeEntity;


    private String name;
    private String content;

    @Enumerated(EnumType.STRING)
    private IssueStatus status;

}
