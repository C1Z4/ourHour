package com.ourhour.domain.chat.controller;

import com.ourhour.domain.chat.dto.ChatMessageDTO;
import com.ourhour.domain.chat.dto.ChatParticipantDTO;
import com.ourhour.domain.chat.dto.ChatRoomDTO;
import com.ourhour.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    @GetMapping
    public List<ChatRoomDTO> getAllChatRooms(Long id/*토큰 정보*/) {

        return chatService.findAllChatRooms(id);
    }

    @PostMapping
    public void registerChatRoom(@RequestBody ChatRoomDTO newChatRoom) {

        chatService.registerChatRoom(newChatRoom);
    }

    @PutMapping("/{roomId}")
    public void modifyChatRoom(
            @PathVariable Long roomId,
            @RequestBody ChatRoomDTO chatRoomUpdateRequest) {

        chatService.modifyChatRoom(roomId, chatRoomUpdateRequest);
    }

    @DeleteMapping("/{roomId}")
    public void deleteChatRoom(@PathVariable Long roomId) {

        chatService.deleteChatRoom(roomId);
    }

    @GetMapping("/{roomId}/messages")
    public List<ChatMessageDTO> getMessages(@PathVariable Long roomId) {

        return chatService.findAllMessages(roomId);
    }

    @GetMapping("/{roomId}/participants")
    public List<ChatParticipantDTO> getChatRoomParticipants(@PathVariable Long roomId) {

        return chatService.findAllParticipants(roomId);
    }
}
