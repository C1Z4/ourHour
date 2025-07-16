package com.ourhour.domain.chat.exceptions;

import com.ourhour.global.exception.BusinessException;

public class ChatException extends BusinessException {

    public ChatException(int status, String message) {
        super(status, message);
    }

    // 존재하지 않는 채팅방일 때
    public static ChatException chatRoomNotFound() {
        return new ChatException(404, "존재하지 않는 채팅방입니다.");
    }

    // 채팅방의 참여자가 아닐 때
    public static ChatException notParticipated() {
        return new ChatException(403, "해당 채팅방의 참여자가 아닙니다.");
    }

    // 이미 참여하고 있는 채팅방일 때
    public static ChatException alreadyParticipated() {
        return new ChatException(409, "이미 참여하고 있는 채팅방입니다.");
    }
}
