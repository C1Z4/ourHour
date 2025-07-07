package com.ourhour.domain.org.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class OrgParticipantMemberId implements Serializable {

    private Long orgId;
    private Long memberId;

}
