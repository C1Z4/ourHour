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
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;
import com.ourhour.domain.org.repository.OrgRepository;
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
    private final OrgRepository orgRepository;
    private final OrgParticipantMemberRepository orgParticipantMemberRepository;

    public List<ChatRoomListResDTO> findAllChatRooms(Long orgId, Long memberId) {

        List<ChatParticipantEntity> participants = chatParticipantRepository.findChatRoomsByOrgAndMember(orgId, memberId);

        return participants.stream()
                .map(participant -> {
                    ChatRoomEntity chatRoom = participant.getChatRoomEntity();

                    return ChatRoomListResDTO.builder()
                            .roomId(chatRoom.getRoomId())
                            .name(chatRoom.getName())
                            .build();
                }).collect(Collectors.toList());
    }

    @Transactional
    public void registerChatRoom(Long orgId, ChatRoomCreateReqDTO request) {

        OrgEntity orgEntity = orgRepository.findById(orgId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회사입니다."));

        ChatRoomEntity newChatRoom = ChatRoomEntity.builder()
                .name(request.getName())
                .color(request.getColor())
                .orgEntity(orgEntity)
                .build();
        chatRoomRepository.save(newChatRoom);

        List<MemberEntity> membersToInvite = memberRepository.findAllById(request.getMemberIds());

        membersToInvite.forEach(member -> {
            ChatParticipantEntity participant = ChatParticipantEntity.createParticipant(newChatRoom, member);
            chatParticipantRepository.save(participant);
        });
    }

    @Transactional
    public void modifyChatRoom(Long orgId, Long roomId, ChatRoomUpdateReqDTO request) {

        ChatRoomEntity targetChatRoom = chatRoomRepository.findByOrgEntity_OrgIdAndRoomId(orgId, roomId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
        targetChatRoom.update(request.getName(), request.getColor());
    }

    @Transactional
    public void deleteChatRoom(Long orgId, Long roomId) {

        chatRoomRepository.deleteByOrgEntity_OrgIdAndRoomId(orgId, roomId);
    }

    public List<ChatMessageResDTO> findAllMessages(Long orgId, Long roomId) {

        return chatMessageRepository.findAllByOrgAndChatRoom(orgId, roomId);
    }

    public List<ChatParticipantResDTO> findAllParticipants(Long orgId, Long roomId) {

        List<ChatParticipantEntity> participants = chatParticipantRepository.findParticipantsByOrgAndRoom(orgId, roomId);

        return participants.stream()
                .map(chatParticipant -> {
                    MemberEntity member = chatParticipant.getMemberEntity();

                    return ChatParticipantResDTO.builder()
                            .memberId(member.getMemberId())
                            .memberName(member.getName())
                            .profileImageUrl(member.getProfileImgUrl())
                            .build();
                }).collect(Collectors.toList());
    }

    @Transactional
    public void addChatRoomParticipant(Long orgId, Long roomId, Long memberId) {

        ChatRoomEntity chatRoom = chatRoomRepository.findByOrgEntity_OrgIdAndRoomId(orgId, roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없거나 해당 조직의 채팅방이 아닙니다."));

        boolean isOrgMember = orgParticipantMemberRepository.existsByOrgEntity_OrgIdAndMemberEntity_MemberId(orgId, memberId);
        if (!isOrgMember) {
            throw new IllegalArgumentException("해당 조직에 속한 멤버가 아닙니다.");
        }

        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버를 찾을 수 없습니다."));

        ChatParticipantEntity newParticipant = ChatParticipantEntity.createParticipant(chatRoom, member);
        chatParticipantRepository.save(newParticipant);
    }

    @Transactional
    public void deleteChatRoomParticipant(Long orgId, Long roomId, Long memberId) {

        ChatParticipantEntity participantToDelete = chatParticipantRepository.findParticipantToDelete(orgId, roomId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 참여자를 찾을 수 없습니다."));

        chatParticipantRepository.delete(participantToDelete);
    }

    @Transactional
    public ChatMessageResDTO saveAndConvertMessage(ChatMessageReqDTO chatMessageReqDTO) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatMessageReqDTO.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        MemberEntity sender = memberRepository.findById(chatMessageReqDTO.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));

        ChatMessageEntity newMessage = ChatMessageEntity.builder()
                .chatRoomEntity(chatRoom)
                .memberEntity(sender)
                .content(chatMessageReqDTO.getMessage())
                .build();

        ChatMessageEntity savedEntity = chatMessageRepository.save(newMessage);

        return new ChatMessageResDTO(
                savedEntity.getChatRoomEntity().getRoomId(),
                savedEntity.getChatMessageId(),
                savedEntity.getMemberEntity().getMemberId(),
                savedEntity.getMemberEntity().getName(),
                savedEntity.getContent(),
                savedEntity.getSentAt()
        );
    }
}