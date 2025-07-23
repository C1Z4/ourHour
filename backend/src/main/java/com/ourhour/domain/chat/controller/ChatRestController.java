package com.ourhour.domain.chat.controller;

import com.ourhour.domain.chat.dto.*;
import com.ourhour.domain.chat.service.ChatService;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.exception.BusinessException;
import com.ourhour.global.jwt.annotation.OrgAuth;
import com.ourhour.global.jwt.annotation.OrgId;
import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.jwt.util.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/orgs/{orgId}/chat-rooms")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    private Long getMemberIdForOrg(Claims claims, Long orgId) {
        return claims.getOrgAuthorityList().stream()
                .filter(auth -> auth.getOrgId().equals(orgId))
                .map(auth -> auth.getMemberId())
                .findFirst()
                .orElseThrow(() -> BusinessException.forbidden("해당 조직의 멤버가 아닙니다."));
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @GetMapping
    public ResponseEntity<ApiResponse<List<ChatRoomListResDTO>>> getAllChatRooms(
            @OrgId @PathVariable Long orgId) {

        Claims claims = UserContextHolder.get();
        Long memberId = getMemberIdForOrg(claims, orgId);
        List<ChatRoomListResDTO> chatRooms = chatService.findAllChatRooms(orgId, memberId);

        return ResponseEntity.ok(ApiResponse.success(chatRooms, "채팅방 목록 조회에 성공했습니다."));
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<ChatRoomDetailResDTO>> getChatRoom(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long roomId
    ) {

        ChatRoomDetailResDTO chatRoom = chatService.findChatRoom(orgId, roomId);

        return ResponseEntity.ok(ApiResponse.success(chatRoom, "{roomId}번 채팅방 조회에 성공했습니다."));
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> registChatRoom(
            @OrgId @PathVariable Long orgId,
            @RequestBody ChatRoomCreateReqDTO request) {

        chatService.registerChatRoom(orgId, request);

        return ResponseEntity.ok(ApiResponse.success(null, "채팅방 생성에 성공했습니다."));
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @PutMapping("/{roomId}")
    public ResponseEntity<ApiResponse<Void>> modifyChatRoom(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long roomId,
            @RequestBody ChatRoomUpdateReqDTO request) {

        chatService.modifyChatRoom(orgId, roomId, request);

        return ResponseEntity.ok(ApiResponse.success(null, "채팅방 수정에 성공했습니다."));
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @DeleteMapping("/{roomId}")
    public ResponseEntity<ApiResponse<Void>> deleteChatRoom(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long roomId) {

        chatService.deleteChatRoom(orgId, roomId);

        return ResponseEntity.ok(ApiResponse.success(null, "채팅방 삭제에 성공했습니다."));
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<ApiResponse<List<ChatMessageResDTO>>> getMessages(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long roomId) {

        List<ChatMessageResDTO> chatMessages = chatService.findAllMessages(orgId, roomId);

        return ResponseEntity.ok(ApiResponse.success(chatMessages, "채팅 메시지 조회에 성공했습니다."));
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @GetMapping("/{roomId}/participants")
    public ResponseEntity<ApiResponse<List<ChatParticipantResDTO>>> getChatRoomParticipants(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long roomId) {

        List<ChatParticipantResDTO> participants = chatService.findAllParticipants(orgId, roomId);

        return ResponseEntity.ok(ApiResponse.success(participants, "채팅방 참여자 목록 조회에 성공했습니다."));
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @PostMapping("/{roomId}/participants")
    public ResponseEntity<ApiResponse<Void>> addChatRoomParticipant(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long roomId,
            @RequestBody ParticipantAddReqDTO request) {

        chatService.addChatRoomParticipants(orgId, roomId, request.getMemberIds());

        return ResponseEntity.ok(ApiResponse.success(null, "참여자 추가에 성공했습니다."));
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @DeleteMapping("/{roomId}/participants/{memberId}")
    public ResponseEntity<ApiResponse<Void>> deleteChatRoomParticipant(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long roomId,
            @PathVariable Long memberId) {

        chatService.deleteChatRoomParticipant(orgId, roomId, memberId);

        return ResponseEntity.ok(ApiResponse.success(null, "참여자 삭제에 성공했습니다."));
    }
}
