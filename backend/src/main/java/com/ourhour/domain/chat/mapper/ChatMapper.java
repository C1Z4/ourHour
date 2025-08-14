package com.ourhour.domain.chat.mapper;

import com.ourhour.domain.chat.dto.ChatMessageResDTO;
import com.ourhour.domain.chat.dto.ChatParticipantResDTO;
import com.ourhour.domain.chat.dto.ChatRoomListResDTO;
import com.ourhour.domain.chat.dto.ChatRoomDetailResDTO;
import com.ourhour.domain.chat.entity.ChatMessageEntity;
import com.ourhour.domain.chat.entity.ChatParticipantEntity;
import com.ourhour.domain.chat.entity.ChatRoomEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    @Mapping(source = "roomId", target = "roomId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "color", target = "color")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "orgEntity.orgId", target = "orgId")
    ChatRoomDetailResDTO toChatRoomResDTO(ChatRoomEntity chatRoom);

    @Mapping(source = "chatRoomEntity.roomId", target = "roomId")
    @Mapping(source = "chatRoomEntity.name", target = "name")
    @Mapping(source = "chatRoomEntity.color", target = "color")
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