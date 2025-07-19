package com.ourhour.domain.board.mapper;

import com.ourhour.domain.board.dto.BoardResponseDTO;
import com.ourhour.domain.board.entity.BoardEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BoardMapper {

    // Entity -> DTO
    BoardResponseDTO toBoardResponseDTO(BoardEntity boardEntity);
}
