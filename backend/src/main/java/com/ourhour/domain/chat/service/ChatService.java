package com.ourhour.domain.chat.service;

import com.ourhour.domain.chat.dto.*;
import com.ourhour.domain.chat.entity.ChatMessageEntity;
import com.ourhour.domain.chat.entity.ChatParticipantEntity;
import com.ourhour.domain.chat.entity.ChatRoomEntity;
import com.ourhour.domain.chat.repository.ChatMessageRepository;
import com.ourhour.domain.chat.repository.ChatParticipantRepository;
import com.ourhour.domain.chat.repository.ChatRoomRepository;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    public List<ChatRoomListResDTO> findAllChatRooms(Long memberId) {
        List<ChatParticipantEntity> participants = chatParticipantRepository.findByMember_MemberId(memberId);

        return participants.stream()
                .map(participant -> {
                    ChatRoomEntity chatRoom = participant.getChatRoom();
                    return ChatRoomListResDTO.builder()
                            .roomId(chatRoom.getRoomId())
                            .name(chatRoom.getName())
                            .build();
                }).collect(Collectors.toList());
    }

    @Transactional
    public void registerChatRoom(ChatRoomCreateReqDTO request) {
        ChatRoomEntity newChatRoom = ChatRoomEntity.builder()
                .name(request.getName())
                .build();
        chatRoomRepository.save(newChatRoom);

        List<MemberEntity> membersToInvite = memberRepository.findAllById(request.getMemberIds());
        membersToInvite.forEach(member -> {
            ChatParticipantEntity participant = ChatParticipantEntity.createParticipant(newChatRoom, member);
            chatParticipantRepository.save(participant);
        });
    }

    @Transactional
    public void modifyChatRoom(Long roomId, ChatRoomUpdateReqDTO request) {
        ChatRoomEntity targetChatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방을 찾을 수 없습니다. id=" + roomId));

        targetChatRoom.update(request.getName(), request.getColor());
    }

    @Transactional
    public void deleteChatRoom(Long roomId) {
        chatRoomRepository.deleteById(roomId);
    }

    public List<ChatMessageResDTO> findAllMessages(Long roomId) {
        List<ChatMessageEntity> messages = chatMessageRepository.findAllByRoomId_RoomId(roomId);

        return messages.stream()
                .map(chatMessage -> {
                    return ChatMessageResDTO.builder()
                            .chatMessageId(chatMessage.getChatMessageId())
                            .senderId(chatMessage.getSenderId().getMemberId())
                            .senderName(chatMessage.getSenderId().getName())
                            .message(chatMessage.getContent())
                            .timestamp(chatMessage.getSentAt())
                            .build();
                }).collect(Collectors.toList());
    }

    public List<ChatParticipantResDTO> findAllParticipants(Long roomId) {
        List<ChatParticipantEntity> participants = chatParticipantRepository.findAllByChatRoom_RoomId(roomId);

        return participants.stream()
                .map(chatParticipant -> {
                    MemberEntity member = chatParticipant.getMember();
                    return ChatParticipantResDTO.builder()
                            .memberId(member.getMemberId())
                            .memberName(member.getName())
                            .profileImageUrl(member.getProfileImgUrl())
                            .build();
                }).collect(Collectors.toList());
    }
}