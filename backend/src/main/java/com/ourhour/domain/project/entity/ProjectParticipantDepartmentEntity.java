package com.ourhour.domain.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_project_participant_department")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectParticipantDepartmentEntity {

    @EmbeddedId
    private ProjectParticipantDepartmentId projectParticipantDepartmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @MapsId("projectId")
    private ProjectEntity project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id")
    @MapsId("deptId")
    private DepartmentEntity department;

}
