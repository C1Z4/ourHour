package com.ourhour.domain.chat.controller;

import com.ourhour.domain.chat.dto.ChatMessageReqDTO;
import com.ourhour.domain.chat.dto.ChatMessageResDTO;
import com.ourhour.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void message(@Payload ChatMessageReqDTO chatMessageReqDTO) {

        ChatMessageResDTO chatMessageResDTO = chatService.saveAndConvertMessage(chatMessageReqDTO);

        messagingTemplate.convertAndSend(
                "/sub/chat/room/" + chatMessageResDTO.getChatRoomId(), chatMessageResDTO
        );    }
}