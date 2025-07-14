package com.ourhour.domain.chat.controller;

import com.ourhour.domain.chat.dto.*;
import com.ourhour.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orgs/{orgId}/chat-rooms")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    @GetMapping
    public List<ChatRoomListResDTO> getAllChatRooms(
            @PathVariable Long orgId,
            @RequestParam Long memberId) {

        return chatService.findAllChatRooms(orgId, memberId);
    }

    @PostMapping
    public void registChatRoom(
            @PathVariable Long orgId,
            @RequestBody ChatRoomCreateReqDTO request) {

        chatService.registerChatRoom(orgId, request);
    }

    @PutMapping("/{roomId}")
    public void modifyChatRoom(
            @PathVariable Long orgId,
            @PathVariable Long roomId,
            @RequestBody ChatRoomUpdateReqDTO request) {

        chatService.modifyChatRoom(orgId, roomId, request);
    }

    @DeleteMapping("/{roomId}")
    public void deleteChatRoom(
            @PathVariable Long orgId,
            @PathVariable Long roomId) {

        chatService.deleteChatRoom(orgId, roomId);
    }

    @GetMapping("/{roomId}/messages")
    public List<ChatMessageResDTO> getMessages(
            @PathVariable Long orgId,
            @PathVariable Long roomId) {

        return chatService.findAllMessages(orgId, roomId);
    }

    @GetMapping("/{roomId}/participants")
    public List<ChatParticipantResDTO> getChatRoomParticipants(
            @PathVariable Long orgId,
            @PathVariable Long roomId) {

        return chatService.findAllParticipants(orgId, roomId);
    }

    @PostMapping("/{roomId}/participants")
    public void addChatRoomParticipant(
            @PathVariable Long orgId,
            @PathVariable Long roomId,
            @RequestBody ParticipantAddReqDTO request) {

        chatService.addChatRoomParticipant(orgId, roomId, request.getMemberId());
    }

    @DeleteMapping("/{roomId}/participants/{memberId}")
    public void deleteChatRoomParticipant(
            @PathVariable Long orgId,
            @PathVariable Long roomId,
            @PathVariable Long memberId) {

        chatService.deleteChatRoomParticipant(orgId, roomId, memberId);
    }
}
