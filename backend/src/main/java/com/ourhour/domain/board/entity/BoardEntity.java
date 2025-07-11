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

    /* 엔티티 생성 로직을 한 곳에서 관리하기 위한 정적 팩토리 메소 */
    public static BoardEntity createBoard(String name, boolean isFixed, OrgEntity orgEntity) {
        BoardEntity board = new BoardEntity();
        board.name = name;
        board.isFixed = isFixed;
        board.orgEntity = orgEntity;
        return board;
    }

    /* 엔티티의 데이터를 수정하는 비즈니스 로직, 객체의 상태 변경을 이 메소드를 통해서만 하도록 하여 일관성을 유지*/
    public void update(String name, boolean isFixed) {
        this.name = name;
        this.isFixed = isFixed;
    }


}

