package com.ourhour.domain.board.dto;

import com.ourhour.domain.board.entity.BoardEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardDTO {

    /**
     * 게시판의 고유 식별자(ID).
     * - 응답(Response) 시에는 항상 값이 채워집니다.
     * - 생성 요청(Request) 시에는 값이 비어있습니다.
     */
    private Long boardId;

    /* 게시판의 이름 */
    private String name;

    /* 게시판 고정 여부 */
    private boolean isFixed;

    /* BoardEntity 객체를 받아 DTO 객체를 생성하는 생성자.
     * 주로 서비스 계층에서 엔티티를 DTO로 변환하여 컨트롤러에 반환할 때 사용됩니다.
     * @param entity 데이터베이스에서 조회한 BoardEntity 객체
     */
    public BoardDTO(BoardEntity entity) {
        this.boardId = entity.getBoardId();
        this.name = entity.getName();
        this.isFixed = entity.isFixed();
    }

}
