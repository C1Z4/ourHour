package com.ourhour.domain.project.entity;

import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.project.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_project")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    private OrgEntity orgEntity;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "projectEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MilestoneEntity> milestoneEntityList = new ArrayList<>();

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
    
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    public Long getOrgId() {
        return orgEntity != null ? orgEntity.getOrgId() : 0;
    }
}
