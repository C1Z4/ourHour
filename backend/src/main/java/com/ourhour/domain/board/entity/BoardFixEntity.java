package com.ourhour.domain.board.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class BoardFixEntity implements Serializable {

    @Column(name = "board_id")
    private Long boardId;

    // 구성원에서 가져온 멤버 아이디
    @Column(name = "member_id")
    private Long memberId;

}
