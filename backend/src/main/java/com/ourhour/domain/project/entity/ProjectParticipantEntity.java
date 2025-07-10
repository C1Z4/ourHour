package com.ourhour.domain.project.entity;

import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
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
    @JoinColumns({
            @JoinColumn(name = "member_id", referencedColumnName = "member_id"),
            @JoinColumn(name = "org_id", referencedColumnName = "org_id")
    })
    @MapsId("orgParticipantMemberId")
    private OrgParticipantMemberEntity orgParticipantMemberEntity;

    public MemberEntity getMemberEntity() {
        return orgParticipantMemberEntity != null ? orgParticipantMemberEntity.getMemberEntity() : null;
    }
}