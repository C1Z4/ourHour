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

import java.time.LocalDate;

@Entity
@Table(name = "tbl_org_participant_member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
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

    private LocalDate joinedAt;

    private LocalDate leftAt;

    public void changeRole(Role newRole) {
        this.role = newRole;
    }

    public void changeStatus(Status status) {
        this.status = status;
    }

    public void markLeftNow() {
        this.leftAt = LocalDate.now();
    }
}
