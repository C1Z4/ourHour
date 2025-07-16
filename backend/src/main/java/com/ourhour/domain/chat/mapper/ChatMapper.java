package com.ourhour.domain.chat.mapper;

import com.ourhour.domain.chat.dto.ChatMessageResDTO;
import com.ourhour.domain.chat.dto.ChatParticipantResDTO;
import com.ourhour.domain.chat.dto.ChatRoomListResDTO;
import com.ourhour.domain.chat.entity.ChatMessageEntity;
import com.ourhour.domain.chat.entity.ChatParticipantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    @Mapping(source = "chatRoomEntity.roomId", target = "roomId")
    @Mapping(source = "chatRoomEntity.name", target = "name")
    ChatRoomListResDTO toChatRoomListResDTO(ChatParticipantEntity participant);

    @Mapping(source = "memberEntity.memberId", target = "memberId")
    @Mapping(source = "memberEntity.name", target = "memberName")
    @Mapping(source = "memberEntity.profileImgUrl", target = "profileImageUrl")
    ChatParticipantResDTO toChatParticipantResDTO(ChatParticipantEntity participant);

    @Mapping(source = "chatRoomEntity.roomId", target = "chatRoomId")
    @Mapping(source = "chatMessageId", target = "chatMessageId")
    @Mapping(source = "memberEntity.memberId", target = "senderId")
    @Mapping(source = "memberEntity.name", target = "senderName")
    @Mapping(source = "content", target = "message")
    @Mapping(source = "sentAt", target = "timestamp")
    ChatMessageResDTO toChatMessageResDTO(ChatMessageEntity chatMessage);
}