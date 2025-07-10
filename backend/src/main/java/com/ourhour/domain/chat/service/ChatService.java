package com.ourhour.domain.chat.service;

import com.ourhour.domain.chat.dto.ChatMessageDTO;
import com.ourhour.domain.chat.dto.ChatParticipantDTO;
import com.ourhour.domain.chat.dto.ChatRoomDTO;
import com.ourhour.domain.chat.entity.ChatParticipantEntity;
import com.ourhour.domain.chat.entity.ChatRoomEntity;
import com.ourhour.domain.chat.mapper.ChatMapper;
import com.ourhour.domain.chat.repository.ChatMessageRepository;
import com.ourhour.domain.chat.repository.ChatParticipantRepository;
import com.ourhour.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMapper chatMapper;

    public List<ChatRoomDTO> findAllChatRooms(Long memberId) {

        List<ChatParticipantEntity> participants = chatParticipantRepository.findAllByMemberIdWithChatRoom(memberId);

        List<ChatRoomDTO> chatRoomList = participants.stream()
                .map(participant -> {
                    return ChatRoomDTO.builder()
                            .roomId(participant.getChatRoomEntity().getRoomId())
                            .name(participant.getChatRoomEntity().getName())
                            .color(participant.getChatRoomEntity().getColor())
                            .createdAt(participant.getChatRoomEntity().getCreatedAt())
                            .build();
                }).toList();

        return chatRoomList;
    }

    @Transactional
    public void registerChatRoom(ChatRoomDTO chatRoom) {

        ChatRoomEntity chatRoomEntity = chatMapper.toChatRoomEntity(chatRoom);

        chatRoomRepository.save(chatRoomEntity);
    }

    @Transactional
    public void modifyChatRoom(Long roomId, ChatRoomDTO chatRoom) {

        ChatRoomEntity targetChatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방을 찾을 수 없습니다. id=" + roomId));

        targetChatRoom.update(chatRoom.getName(), chatRoom.getColor());
    }

    @Transactional
    public void deleteChatRoom(Long roomId) {

        chatRoomRepository.deleteById(roomId);
    }

    public List<ChatMessageDTO> findAllMessages(Long roomId) {

        List<ChatMessageDTO> chatMessageList = chatMessageRepository.findAllByRoomId_RoomId(roomId).stream()
                .map(chatMessage -> {
                    return ChatMessageDTO.builder()
                            .chatRoomId(chatMessage.getRoomId().getRoomId())
                            .chatMessageId(chatMessage.getChatMessageId())
                            .senderId(chatMessage.getSenderId().getMemberId())
                            .message(chatMessage.getContent())
                            .timestamp(chatMessage.getSentAt())
                            .build();
                }).toList();

        System.out.println("chatMessageList = " + chatMessageList);

        return chatMessageList;
    }

    public List<ChatParticipantDTO> findAllParticipants(Long roomId) {

        List<ChatParticipantDTO> chatParticipantList = chatParticipantRepository.findAllByChatRoomEntity_RoomId(roomId).stream()
                .map(chatParticipant -> {
                    return ChatParticipantDTO.builder()
                            .roomId(chatParticipant.getChatRoomEntity().getRoomId())
                            .memberId(chatParticipant.getMemberEntity().getMemberId())
                            .build();
                }).toList();

        System.out.println("chatParticipantList = " + chatParticipantList);

        return chatParticipantList;
    }
}