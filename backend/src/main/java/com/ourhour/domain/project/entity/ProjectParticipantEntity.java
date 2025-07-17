package com.ourhour.domain.project.entity;

import com.ourhour.domain.member.entity.MemberEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_project_participant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectParticipantEntity {

    @EmbeddedId
    private ProjectParticipantId projectParticipantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @MapsId("projectId")
    private ProjectEntity projectEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @MapsId("memberId")
    private MemberEntity memberEntity;

    @Builder
    public ProjectParticipantEntity(ProjectParticipantId projectParticipantId,
            ProjectEntity projectEntity,
            MemberEntity memberEntity) {
        this.projectParticipantId = projectParticipantId;
        this.projectEntity = projectEntity;
        this.memberEntity = memberEntity;
    }

    public String getDeptName() {
        return memberEntity.getOrgParticipantMemberEntityList().stream()
                .filter(opm -> opm.getOrgEntity().getOrgId().equals(projectEntity.getOrgEntity().getOrgId()))
                .filter(opm -> opm.getDepartmentEntity() != null)
                .map(opm -> opm.getDepartmentEntity().getName())
                .findFirst()
                .orElse(null);
    }

    public String getPositionName() {
        return memberEntity.getOrgParticipantMemberEntityList().stream()
                .filter(opm -> opm.getOrgEntity().getOrgId().equals(projectEntity.getOrgEntity().getOrgId()))
                .filter(opm -> opm.getPositionEntity() != null)
                .map(opm -> opm.getPositionEntity().getName())
                .findFirst()
                .orElse(null);
    }
}