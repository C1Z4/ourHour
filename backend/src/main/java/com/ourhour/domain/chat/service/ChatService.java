package com.ourhour.domain.chat.service;

import com.ourhour.domain.chat.dto.*;
import com.ourhour.domain.chat.entity.ChatMessageEntity;
import com.ourhour.domain.chat.entity.ChatParticipantEntity;
import com.ourhour.domain.chat.entity.ChatRoomEntity;
import com.ourhour.domain.chat.exceptions.ChatException;
import com.ourhour.domain.chat.mapper.ChatMapper;
import com.ourhour.domain.chat.repository.ChatMessageRepository;
import com.ourhour.domain.chat.repository.ChatParticipantRepository;
import com.ourhour.domain.chat.repository.ChatRoomRepository;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;
import com.ourhour.domain.org.repository.OrgRepository;
import com.ourhour.global.exception.BusinessException;
import com.ourhour.global.jwt.dto.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatMapper chatMapper;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final OrgRepository orgRepository;
    private final OrgParticipantMemberRepository orgParticipantMemberRepository;

    public List<ChatRoomListResDTO> findAllChatRooms(Long orgId, Long memberId) {

        List<ChatParticipantEntity> participants = chatParticipantRepository.findChatRoomsByOrgAndMember(orgId, memberId);

        return participants.stream()
                .map(chatMapper::toChatRoomListResDTO)
                .collect(Collectors.toList());
    }

    public ChatRoomDetailResDTO findChatRoom(Long orgId, Long roomId) {

        ChatRoomEntity chatRoomEntity = chatRoomRepository.findByOrgEntity_OrgIdAndRoomId(orgId, roomId)
                .orElseThrow(ChatException::chatRoomNotFound);

        return chatMapper.toChatRoomResDTO(chatRoomEntity);
    }

    @Transactional
    public void registerChatRoom(Long orgId, ChatRoomCreateReqDTO request) {

        OrgEntity orgEntity = orgRepository.findById(orgId).orElseThrow(() -> BusinessException.notFound("존재하지 않는 회사입니다."));

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
                ChatException::chatRoomNotFound);
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
                .map(chatMapper::toChatParticipantResDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addChatRoomParticipant(Long orgId, Long roomId, Long memberId) {

        ChatRoomEntity chatRoom = chatRoomRepository.findByOrgEntity_OrgIdAndRoomId(orgId, roomId)
                .orElseThrow(ChatException::chatRoomNotFound);

        boolean isOrgMember = orgParticipantMemberRepository.existsByOrgEntity_OrgIdAndMemberEntity_MemberId(orgId, memberId);
        if (!isOrgMember) {
            throw BusinessException.forbidden("해당 회사의 멤버가 아닙니다.");
        }

        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> BusinessException.badRequest("해당 멤버를 찾을 수 없습니다."));

        ChatParticipantEntity newParticipant = ChatParticipantEntity.createParticipant(chatRoom, member);
        chatParticipantRepository.save(newParticipant);
    }

    @Transactional
    public void deleteChatRoomParticipant(Long orgId, Long roomId, Long memberId) {

        ChatParticipantEntity participantToDelete = chatParticipantRepository.findParticipantToDelete(orgId, roomId, memberId)
                .orElseThrow(ChatException::notParticipated);

        chatParticipantRepository.delete(participantToDelete);
    }

    @Transactional
    public ChatMessageResDTO saveAndConvertMessage(ChatMessageReqDTO chatMessageReqDTO, Claims claims) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatMessageReqDTO.getChatRoomId())
                .orElseThrow(ChatException::chatRoomNotFound);

        Long orgId = chatRoom.getOrgEntity().getOrgId();
        Long memberId = claims.getOrgAuthorityList().stream()
                .filter(auth -> auth.getOrgId().equals(orgId))
                .map(auth -> auth.getMemberId())
                .findFirst()
                .orElseThrow(() -> BusinessException.forbidden("해당 회사의 멤버가 아닙니다."));

        MemberEntity sender = memberRepository.findById(memberId)
                .orElseThrow(() -> BusinessException.notFound("메시지를 보낼 사용자 정보를 찾을 수 없습니다."));

        ChatMessageEntity newMessage = ChatMessageEntity.builder()
                .chatRoomEntity(chatRoom)
                .memberEntity(sender)
                .content(chatMessageReqDTO.getMessage())
                .sentAt(LocalDateTime.now())
                .build();

        ChatMessageEntity savedEntity = chatMessageRepository.save(newMessage);

        return chatMapper.toChatMessageResDTO(savedEntity);
    }
}