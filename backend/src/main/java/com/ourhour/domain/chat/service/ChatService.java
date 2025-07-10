package com.ourhour.domain.chat.service;

import com.ourhour.domain.chat.dto.ChatRoomDTO;
import com.ourhour.domain.chat.entity.ChatParticipantEntity;
import com.ourhour.domain.chat.repository.ChatMessageRepository;
import com.ourhour.domain.chat.repository.ChatParticipantRepository;
import com.ourhour.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatParticipantRepository chatParticipantRepository;

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
}