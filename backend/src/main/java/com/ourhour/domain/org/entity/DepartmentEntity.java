package com.ourhour.domain.org.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_department")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepartmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deptId;

    @OneToMany(mappedBy = "departmentEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrgParticipantMemberEntity> orgParticipantMemberEntityList = new ArrayList<>();

    private String name;

}
