package com.ourhour.domain.project.entity;

import jakarta.persistence.*;
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
    private ProjectEntity project;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "member_id")
     @MapsId("memberId")
     private MemberEntity member;

}