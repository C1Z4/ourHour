package com.ourhour.domain.project.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_milestone")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MilestoneEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long milestoneId;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "milestoneEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IssueEntity> issueEntityList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private ProjectEntity projectEntity;

    @Setter
    private String name;

    private byte progress = 0;

    @Builder
    public MilestoneEntity(ProjectEntity projectEntity, String name, byte progress) {
        this.projectEntity = projectEntity;
        this.name = name;
        this.progress = progress;
    }
}
