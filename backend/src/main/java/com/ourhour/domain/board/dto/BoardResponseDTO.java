package com.ourhour.domain.board.dto;

import com.ourhour.domain.board.entity.BoardEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class BoardResponseDTO {

    private Long boardId;
    private String name;

}
