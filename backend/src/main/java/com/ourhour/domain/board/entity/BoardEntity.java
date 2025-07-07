package com.ourhour.domain.board.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private OrgEntity orgEntity;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private boolean isFixed;


}

