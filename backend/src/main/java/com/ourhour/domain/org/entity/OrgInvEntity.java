package com.ourhour.domain.org.entity;

import com.ourhour.domain.auth.entity.AbstractVerificationEntity;
import com.ourhour.domain.org.enums.InvStatus;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


import java.time.LocalDateTime;

@Entity
@Table(name="tbl_org_invitations")
@SuperBuilder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrgInvEntity extends AbstractVerificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invitationId;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="batch_id")
    private OrgInvBatchEntity orgInvBatchEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accepted_user_id")
    private UserEntity acceptedUserEntity;

    @Setter
    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private InvStatus status;
    private LocalDateTime acceptedAt;

    public static OrgInvEntity create(
            OrgInvBatchEntity batch,
            String token,
            String email,
            Role role,
            LocalDateTime expiredAt
    ) {
        return OrgInvEntity.builder()
                .orgInvBatchEntity(batch)
                .token(token)
                .email(email)
                .role(role)
                .status(InvStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .expiredAt(expiredAt)
                .isUsed(false)
                .build();
    }

    public void changeStatusToAccepted() {
        this.status = InvStatus.ACCEPTED;
    }

    public void changeStatusToExpired() {
        this.status = InvStatus.ACCEPTED;
    }
}
