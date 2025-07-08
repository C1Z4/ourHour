package com.ourhour.domain.board.entity;

import com.ourhour.domain.org.entity.OrgEntity;
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

    private String name;

    private boolean isFixed;


}

