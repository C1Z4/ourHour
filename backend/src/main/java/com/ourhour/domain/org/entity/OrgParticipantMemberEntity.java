package com.ourhour.domain.org.entity;

import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.org.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_org_participant_member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrgParticipantMemberEntity {

    @EmbeddedId
    private OrgParticipantMemberId orgParticipantMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="org_id")
    @MapsId("orgId")
    private OrgEntity orgEntity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    @MapsId("memberId")
    private MemberEntity memberEntity;

    @ManyToOne
    @JoinColumn(name = "dept_id")
    private DepartmentEntity departmentEntity;

    @ManyToOne
    @JoinColumn(name="position_id")
    private PositionEntity positionEntity;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public OrgParticipantMemberEntity(OrgParticipantMemberId orgParticipantMemberId, OrgEntity orgEntity, MemberEntity memberEntity, DepartmentEntity departmentEntity, PositionEntity positionEntity, Role role) {
        this.orgParticipantMemberId = orgParticipantMemberId;
        this.orgEntity = orgEntity;
        this.memberEntity = memberEntity;
        this.departmentEntity = departmentEntity;
        this.positionEntity = positionEntity;
        this.role = role;
    }
}
