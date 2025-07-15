package com.ourhour.domain.chat.controller;

import com.ourhour.domain.chat.dto.*;
import com.ourhour.domain.chat.service.ChatService;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.global.exception.BusinessException;
import com.ourhour.global.jwt.annotation.OrgAuth;
import com.ourhour.global.jwt.annotation.OrgId;
import com.ourhour.global.jwt.dto.Claims;
import com.ourhour.global.jwt.util.AuthorizationUtil;
import com.ourhour.global.jwt.util.UserContextHolder;
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

    private Long getMemberIdForOrg(Claims claims, Long orgId) {
        return claims.getOrgAuthorityList().stream()
                .filter(auth -> auth.getOrgId().equals(orgId))
                .map(auth -> auth.getMemberId())
                .findFirst()
                .orElseThrow(() -> BusinessException.forbidden("해당 조직의 멤버가 아닙니다."));
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @GetMapping
    public List<ChatRoomListResDTO> getAllChatRooms(
            @OrgId @PathVariable Long orgId) {

        Claims claims = UserContextHolder.get();
        Long memberId = getMemberIdForOrg(claims, orgId);

        return chatService.findAllChatRooms(orgId, memberId);
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @PostMapping
    public void registChatRoom(
            @OrgId @PathVariable Long orgId,
            @RequestBody ChatRoomCreateReqDTO request) {

        chatService.registerChatRoom(orgId, request);
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @PutMapping("/{roomId}")
    public void modifyChatRoom(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long roomId,
            @RequestBody ChatRoomUpdateReqDTO request) {

        chatService.modifyChatRoom(orgId, roomId, request);
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @DeleteMapping("/{roomId}")
    public void deleteChatRoom(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long roomId) {

        chatService.deleteChatRoom(orgId, roomId);
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @GetMapping("/{roomId}/messages")
    public List<ChatMessageResDTO> getMessages(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long roomId) {

        return chatService.findAllMessages(orgId, roomId);
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @GetMapping("/{roomId}/participants")
    public List<ChatParticipantResDTO> getChatRoomParticipants(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long roomId) {

        return chatService.findAllParticipants(orgId, roomId);
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @PostMapping("/{roomId}/participants")
    public void addChatRoomParticipant(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long roomId,
            @RequestBody ParticipantAddReqDTO request) {

        chatService.addChatRoomParticipant(orgId, roomId, request.getMemberId());
    }

    @OrgAuth(accessLevel = Role.MEMBER)
    @DeleteMapping("/{roomId}/participants/{memberId}")
    public void deleteChatRoomParticipant(
            @OrgId @PathVariable Long orgId,
            @PathVariable Long roomId,
            @PathVariable Long memberIdToDelete) {

        Claims claims = UserContextHolder.get();
        Long requestingMemberId = getMemberIdForOrg(claims, orgId);

        // 자기 자신이 나가기
        boolean isDeletingSelf = requestingMemberId.equals(memberIdToDelete);

        // 관리자가 다른 멤버를 내보내기
        boolean isAdminDeletingOther = AuthorizationUtil.isHigherThan(claims, orgId, Role.ADMIN);

        if (isDeletingSelf || isAdminDeletingOther) {
            chatService.deleteChatRoomParticipant(orgId, roomId, memberIdToDelete);
        } else {
            throw BusinessException.forbidden("채팅방에서 다른 멤버를 내보낼 권한이 없습니다.");
        }
    }
}
