package com.ourhour.domain.org.entity;

import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.org.enums.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_org_participant_member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrgParticipantMemberEntity {

    @EmbeddedId
    private OrgParticipantMemberId orgParticipantMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="org_id")
    @MapsId("orgId")
    private OrgEntity orgEntity;

    @ManyToOne(fetch = FetchType.LAZY)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.ACTIVE;

    private LocalDateTime joinedAt;

    private LocalDateTime leftAt;

    @Builder
    public OrgParticipantMemberEntity(OrgParticipantMemberId orgParticipantMemberId, OrgEntity orgEntity, MemberEntity memberEntity, DepartmentEntity departmentEntity, PositionEntity positionEntity, Role role) {
        this.orgParticipantMemberId = new OrgParticipantMemberId();
        this.orgEntity = orgEntity;
        this.memberEntity = memberEntity;
        this.departmentEntity = departmentEntity;
        this.positionEntity = positionEntity;
        this.role = role;
    }

    public void changeRole(Role newRole) {
        this.role = newRole;
    }
}
