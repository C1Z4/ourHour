package com.backend.domain.project.entity;

import com.backend.domain.organization.entity.Org;
import com.backend.domain.project.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "tbl_project")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int projectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    private Org org;

    private String name;
    private String description;
    private Date startAt;
    private Date endAt;
    private ProjectStatus status;

    public int getOrgId() {
        return org != null ? org.getOrgId() : 0;
    }
}
