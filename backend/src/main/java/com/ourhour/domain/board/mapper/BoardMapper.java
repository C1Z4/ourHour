package com.ourhour.domain.board.mapper;

import com.ourhour.domain.board.dto.BoardDTO;
import com.ourhour.domain.board.entity.BoardEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {

    // BoardEntity -> BoardDTO로 변환하는 규칙
    BoardDTO toDTO(BoardEntity boardEntity);

    // List<BoardEntity> -> List<BoardDTO>로 변환하는 규칙
    List<BoardDTO> toDTOList(List<BoardEntity> boardEntityList);

}
