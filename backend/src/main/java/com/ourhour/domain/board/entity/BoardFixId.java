package com.ourhour.domain.board.entity;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class BoardFixId implements Serializable {

    private Long boardId;

    // 구성원의 member_id
    private Long memberId;

    }
