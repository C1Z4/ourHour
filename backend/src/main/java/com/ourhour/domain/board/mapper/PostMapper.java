
package com.ourhour.domain.board.mapper;

import com.ourhour.domain.board.dto.PostDTO;
import com.ourhour.domain.board.entity.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PostMapper {


    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    // Entity -> DTO 매핑
    @Mapping(source = "authorEntity.name", target = "authorName")
    @Mapping(source = "boardEntity.boardId", target = "boardId")
    PostDTO toDTO(PostEntity postEntity);
}