package com.ourhour.domain.chat.mapper;

import com.ourhour.domain.chat.dto.ChatRoomDTO;
import com.ourhour.domain.chat.entity.ChatRoomEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    ChatRoomEntity toChatRoomEntity(ChatRoomDTO chatRoomDTO);
}
