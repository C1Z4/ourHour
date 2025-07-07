package com.ourhour.domain.org.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_position")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long positionId;

    @OneToMany(mappedBy="positionEntity")
    private List<OrgParticipantMemberEntity> orgParticipantMemberEntityList = new ArrayList<>();

    private String name;
}
