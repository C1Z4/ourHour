package com.ourhour.domain.project.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class ProjectParticipantId implements Serializable {

    private Long projectId;
    private Long memberId;
}