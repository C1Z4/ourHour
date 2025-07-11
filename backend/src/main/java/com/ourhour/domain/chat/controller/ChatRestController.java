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
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    @GetMapping
    public List<ChatRoomListResDTO> getAllChatRooms(@RequestParam Long memberId /*나중엔 토큰 정보*/) {

        return chatService.findAllChatRooms(memberId);
    }

    @PostMapping
    public void registerChatRoom(@RequestBody ChatRoomCreateReqDTO request) {

        chatService.registerChatRoom(request);
    }

    @PutMapping("/{roomId}")
    public void modifyChatRoom(
            @PathVariable Long roomId,
            @RequestBody ChatRoomUpdateReqDTO request) {

        chatService.modifyChatRoom(roomId, request);
    }

    @DeleteMapping("/{roomId}")
    public void deleteChatRoom(@PathVariable Long roomId) {

        chatService.deleteChatRoom(roomId);
    }

    @GetMapping("/{roomId}/messages")
    public List<ChatMessageResDTO> getMessages(@PathVariable Long roomId) {

        return chatService.findAllMessages(roomId);
    }

    @GetMapping("/{roomId}/participants")
    public List<ChatParticipantResDTO> getChatRoomParticipants(@PathVariable Long roomId) {

        return chatService.findAllParticipants(roomId);
    }

    @PostMapping("/{roomId}/participants")
    public void addChatRoomParticipant(
            @PathVariable Long roomId,
            @RequestBody ParticipantAddReqDTO request) {

        chatService.addChatRoomParticipant(roomId, request.getMemberId());
    }

    @DeleteMapping("/{roomId}/participants/{memberId}")
    public void deleteChatRoomParticipant(
            @PathVariable Long roomId,
            @PathVariable Long memberId) {

        chatService.deleteChatRoomParticipant(roomId, memberId);
    }
}
