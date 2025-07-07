package com.ourhour.domain.org.entity;

import com.ourhour.domain.member.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class OrgParticipantMemberId implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="org_id")
    private OrgEntity orgEntity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private MemberEntity memberEntity;
}
