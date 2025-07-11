package com.ourhour.domain.project.entity;

import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.project.enums.IssueStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Setter;

@Entity
@Table(name = "tbl_issue")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IssueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long issueId;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "milestone_id")
    private MilestoneEntity milestoneEntity;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_tag_id")
    private IssueTagEntity issueTagEntity;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private MemberEntity assigneeEntity;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private ProjectEntity projectEntity;

    @Setter
    private String name;

    @Setter
    private String content;

    @Setter
    @Enumerated(EnumType.STRING)
    private IssueStatus status = IssueStatus.BACKLOG;

    @Builder
    public IssueEntity(Long issueId, MilestoneEntity milestoneEntity, IssueTagEntity issueTagEntity, MemberEntity assigneeEntity, ProjectEntity projectEntity, String name, String content, IssueStatus status) {
        this.issueId = issueId;
        this.milestoneEntity = milestoneEntity;
        this.issueTagEntity = issueTagEntity;
        this.assigneeEntity = assigneeEntity;
        this.projectEntity = projectEntity;
        this.name = name;
        this.content = content;
        this.status = status != null ? status : IssueStatus.BACKLOG;
    }

}
