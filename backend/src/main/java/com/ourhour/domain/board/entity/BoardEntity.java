package com.ourhour.domain.board.entity;

import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.org.entity.OrgEntity;
import jakarta.persistence.*;
import lombok.*;

import javax.net.ssl.SSLSession;

@Entity
@Table(name = "tbl_board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BoardEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private OrgEntity orgEntity;

    private String name;

    private boolean isFixed;

    public void update(String name, boolean isFixed) {
        this.name = name;
        this.isFixed = isFixed;
    }

}




