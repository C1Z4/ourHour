package com.ourhour.domain.project.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
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
    private ProjectParticipantId ProjectParticipantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @MapsId("projectId")
    private ProjectEntity projectEntity;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "member_id")
     @MapsId("memberId")
     private MemberEntity memberEntity;

}