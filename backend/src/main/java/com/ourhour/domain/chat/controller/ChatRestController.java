package com.ourhour.domain.chat.controller;

import com.ourhour.domain.chat.dto.*;
import com.ourhour.domain.chat.service.ChatService;
import com.ourhour.domain.member.exception.MemberException;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.global.common.dto.ApiResponse;
import com.ourhour.global.jwt.annotation.OrgAuth;
import com.ourhour.global.jwt.annotation.OrgId;
import com.ourhour.global.util.SecurityUtil;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/orgs/{orgId}/chat-rooms")
@RequiredArgsConstructor
@Tag(name = "채팅(REST)", description = "채팅방/메시지/참가자 관리 API")
public class ChatRestController {

    private final ChatService chatService;

    @OrgAuth(accessLevel = Role.MEMBER)
    @GetMapping
    @Operation(summary = "채팅방 목록 조회", description = "조직 내 채팅방 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ChatRoomListResDTO>>> getAllChatRooms(
            @OrgId @PathVariable Long orgId) {

        Long memberId = SecurityUtil.getCurrentMemberIdByOrgId(orgId);
        if (memberId == null) {
            throw MemberException.memberAccessDeniedException();
        }

        List<ChatRoomListResDTO> chatRooms = chatService.findAllChatRooms(orgId, memberId);

        return ResponseEntity.ok(ApiResponse.success(chatRooms, "채팅방 목록 조회에 성공했습니다."));
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @GetMapping("/{roomId}")
    @Operation(summary = "채팅방 상세 조회", description = "특정 채팅방 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<ChatRoomDetailResDTO>> getChatRoom(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long roomId) {

        ChatRoomDetailResDTO chatRoom = chatService.findChatRoom(orgId, roomId);

        return ResponseEntity.ok(ApiResponse.success(chatRoom, "{roomId}번 채팅방 조회에 성공했습니다."));
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @PostMapping
    @Operation(summary = "채팅방 생성", description = "새 채팅방을 생성합니다.")
    public ResponseEntity<ApiResponse<Void>> registChatRoom(
            @OrgId @PathVariable Long orgId,
            @RequestBody ChatRoomCreateReqDTO request) {

        chatService.registerChatRoom(orgId, request);

        return ResponseEntity.ok(ApiResponse.success(null, "채팅방 생성에 성공했습니다."));
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @PutMapping("/{roomId}")
    @Operation(summary = "채팅방 수정", description = "채팅방 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> modifyChatRoom(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long roomId,
            @RequestBody ChatRoomUpdateReqDTO request) {

        chatService.modifyChatRoom(orgId, roomId, request);

        return ResponseEntity.ok(ApiResponse.success(null, "채팅방 수정에 성공했습니다."));
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @DeleteMapping("/{roomId}")
    @Operation(summary = "채팅방 삭제", description = "채팅방을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteChatRoom(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long roomId) {

        chatService.deleteChatRoom(orgId, roomId);

        return ResponseEntity.ok(ApiResponse.success(null, "채팅방 삭제에 성공했습니다."));
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @GetMapping("/{roomId}/messages")
    @Operation(summary = "메시지 목록 조회", description = "특정 채팅방의 메시지 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ChatMessageResDTO>>> getMessages(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long roomId) {

        List<ChatMessageResDTO> chatMessages = chatService.findAllMessages(orgId, roomId);

        return ResponseEntity.ok(ApiResponse.success(chatMessages, "채팅 메시지 조회에 성공했습니다."));
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @GetMapping("/{roomId}/participants")
    @Operation(summary = "참여자 목록 조회", description = "특정 채팅방의 참가자 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ChatParticipantResDTO>>> getChatRoomParticipants(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long roomId) {

        List<ChatParticipantResDTO> participants = chatService.findAllParticipants(orgId, roomId);

        return ResponseEntity.ok(ApiResponse.success(participants, "채팅방 참여자 목록 조회에 성공했습니다."));
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @PostMapping("/{roomId}/participants")
    @Operation(summary = "참여자 추가", description = "특정 채팅방에 참가자를 추가합니다.")
    public ResponseEntity<ApiResponse<Void>> addChatRoomParticipant(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long roomId,
            @RequestBody ParticipantAddReqDTO request) {

        chatService.addChatRoomParticipants(orgId, roomId, request.getMemberIds());

        return ResponseEntity.ok(ApiResponse.success(null, "참여자 추가에 성공했습니다."));
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @DeleteMapping("/{roomId}/participants/{memberId}")
    @Operation(summary = "참여자 삭제", description = "특정 채팅방에서 참가자를 제거합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteChatRoomParticipant(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long roomId,
            @PathVariable Long memberId) {

        chatService.deleteChatRoomParticipant(orgId, roomId, memberId);

        return ResponseEntity.ok(ApiResponse.success(null, "참여자 삭제에 성공했습니다."));
    }
}
