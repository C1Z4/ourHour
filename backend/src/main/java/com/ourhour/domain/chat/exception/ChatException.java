package com.ourhour.domain.chat.exception;

import com.ourhour.global.exception.BusinessException;
import com.ourhour.global.exception.ErrorCode;

public class ChatException extends BusinessException {

    public ChatException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChatException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static ChatException chatRoomNotFoundException() {
        return new ChatException(ErrorCode.CHAT_ROOM_NOT_FOUND);
    }

    public static ChatException chatNotParticipantException() {
        return new ChatException(ErrorCode.CHAT_NOT_PARTICIPANT);
    }

    public static ChatException chatAlreadyParticipantException() {
        return new ChatException(ErrorCode.CHAT_ALREADY_PARTICIPANT);
    }

    // 이미 참여하고 있는 채팅방일 때
    public static ChatException alreadyParticipated() {
        return new ChatException(ErrorCode.CHAT_ALREADY_PARTICIPANT);
    }
}