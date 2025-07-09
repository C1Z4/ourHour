package com.ourhour.domain.board.entity;

import com.ourhour.domain.member.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class BoardFixEntity implements Serializable {

    @EmbeddedId
    private BoardFixId id;

    // BoardFix는 하나의 Board에 속함
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("boardId") // BoardFixId의 boardId 필드를 매핑
    @JoinColumn(name = "board_id")
    private BoardEntity boardEntity;

    // BoardFix는 하나의 User에 속함
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId") // BoardFixId의 memberId 필드를 매핑
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;
}
