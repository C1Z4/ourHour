package com.ourhour.domain.org.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_org_invitation_batch")
@Getter
@NoArgsConstructor
public class OrgInvBatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long batchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "org_id", referencedColumnName = "org_id"),
            @JoinColumn(name = "inviter_member_id", referencedColumnName = "member_id")
    })
    private OrgParticipantMemberEntity orgParticipantMemberEntity;

    @OneToMany(mappedBy = "orgInvBatchEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrgInvEntity> orgInvEntityList = new ArrayList<>();

    private LocalDateTime createdAt;

    @Builder
    public OrgInvBatchEntity(OrgParticipantMemberEntity orgParticipantMemberEntity) {
        this.orgParticipantMemberEntity = orgParticipantMemberEntity;
        this.createdAt = LocalDateTime.now();
    }

}
