package com.ourhour.domain.chat.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 사용자의 현재 채팅방 위치를 추적하는 서비스
 * 알림 발송 시 사용자가 해당 채팅방에 있는지 확인하여 불필요한 알림을 방지
 */
@Service
public class UserLocationService {
    
    // 사용자 ID -> 현재 채팅방 ID 매핑
    private final Map<Long, Long> userCurrentChatRoom = new ConcurrentHashMap<>();
    
    /**
     * 사용자가 채팅방에 입장할 때 호출
     * @param userId 사용자 ID
     * @param roomId 채팅방 ID
     */
    public void enterChatRoom(Long userId, Long roomId) {
        userCurrentChatRoom.put(userId, roomId);
    }
    
    /**
     * 사용자가 채팅방에서 나갈 때 호출
     * @param userId 사용자 ID
     */
    public void leaveChatRoom(Long userId) {
        userCurrentChatRoom.remove(userId);
    }
    
    /**
     * 사용자가 특정 채팅방에 있는지 확인
     * @param userId 사용자 ID
     * @param roomId 채팅방 ID
     * @return 사용자가 해당 채팅방에 있으면 true, 아니면 false
     */
    public boolean isUserInChatRoom(Long userId, Long roomId) {
        return Objects.equals(userCurrentChatRoom.get(userId), roomId);
    }
    
    /**
     * 사용자의 현재 채팅방 ID 조회
     * @param userId 사용자 ID
     * @return 현재 채팅방 ID (없으면 null)
     */
    public Long getCurrentChatRoom(Long userId) {
        return userCurrentChatRoom.get(userId);
    }
}