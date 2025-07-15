package com.ourhour.domain.chat.controller;

import com.ourhour.domain.chat.dto.ChatMessageReqDTO;
import com.ourhour.domain.chat.dto.ChatMessageResDTO;
import com.ourhour.domain.chat.service.ChatService;
import com.ourhour.global.jwt.dto.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void message(@Payload ChatMessageReqDTO chatMessageReqDTO, Principal principal) {

        Claims claims = (Claims) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        ChatMessageResDTO chatMessageResDTO = chatService.saveAndConvertMessage(chatMessageReqDTO, claims);

        messagingTemplate.convertAndSend(
                "/sub/chat/room/" + chatMessageResDTO.getChatRoomId(), chatMessageResDTO
        );
    }
}