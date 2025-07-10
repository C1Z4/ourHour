package com.ourhour.domain.project.entity;

import com.ourhour.domain.org.entity.OrgParticipantMemberId;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class ProjectParticipantId implements Serializable {

    private Long projectId;
    private OrgParticipantMemberId orgParticipantMemberId;
}