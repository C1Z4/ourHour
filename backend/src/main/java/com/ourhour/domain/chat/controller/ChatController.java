package com.ourhour.domain.chat.controller;

import com.ourhour.domain.chat.dto.ChatMessageReqDTO;
import com.ourhour.domain.chat.dto.ChatMessageResDTO;
import com.ourhour.domain.chat.dto.ChatRoomEnterReqDTO;
import com.ourhour.domain.chat.dto.ChatRoomLeaveReqDTO;
import com.ourhour.domain.chat.service.ChatService;
import com.ourhour.domain.chat.service.UserLocationService;
import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.util.AsyncUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;
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

        Claims claims = (Claims) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        DelegatingSecurityContextExecutor executor = AsyncUtil.getExecutor();

        executor.execute(() -> {
            ChatMessageResDTO chatMessageResDTO = chatService.saveAndConvertMessage(chatMessageReqDTO, claims);


            messagingTemplate.convertAndSend(
                    "/sub/chat/room/" + chatMessageResDTO.getChatRoomId(), chatMessageResDTO
            );
        });
    }

    @MessageMapping("/chat/enter")
    public void enterChatRoom(@Payload ChatRoomEnterReqDTO request, Principal principal) {
        Claims claims = (Claims) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Long userId = claims.getUserId();
        
        userLocationService.enterChatRoom(userId, request.getRoomId());
    }

    @MessageMapping("/chat/leave")
    public void leaveChatRoom(@Payload ChatRoomLeaveReqDTO request, Principal principal) {
        Claims claims = (Claims) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Long userId = claims.getUserId();
        
        userLocationService.leaveChatRoom(userId);
    }
}