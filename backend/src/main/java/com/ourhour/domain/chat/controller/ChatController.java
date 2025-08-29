package com.ourhour.domain.chat.controller;

import com.ourhour.domain.chat.dto.ChatMessageReqDTO;
import com.ourhour.domain.chat.dto.ChatMessageResDTO;
import com.ourhour.domain.chat.dto.ChatRoomEnterReqDTO;
import com.ourhour.domain.chat.dto.ChatRoomLeaveReqDTO;
import com.ourhour.domain.chat.service.ChatService;
import com.ourhour.domain.chat.service.UserLocationService;
import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.jwt.dto.CustomUserDetails;
import com.ourhour.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;
    private final UserLocationService userLocationService;

    @MessageMapping("/chat/message")
    public void message(@Payload ChatMessageReqDTO chatMessageReqDTO, Principal principal) {

        ChatMessageResDTO chatMessageResDTO = chatService.saveAndConvertMessage(chatMessageReqDTO, principal);

        messagingTemplate.convertAndSend(
                "/sub/chat/room/" + chatMessageResDTO.getChatRoomId(), chatMessageResDTO
        );
    }

    @MessageMapping("/chat/enter")
    public void enterChatRoom(@Payload ChatRoomEnterReqDTO request, Principal principal) {
        Authentication authentication = (Authentication) principal;
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        userLocationService.enterChatRoom(userId, request.getRoomId());
    }

    @MessageMapping("/chat/leave")
    public void leaveChatRoom(@Payload ChatRoomLeaveReqDTO request, Principal principal) {
        Authentication authentication = (Authentication) principal;
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        userLocationService.leaveChatRoom(userId);
    }
}