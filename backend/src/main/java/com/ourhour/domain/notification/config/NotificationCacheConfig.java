package com.ourhour.domain.notification.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Notification 도메인 캐시 설정
 * 읽지 않은 알림 개수를 캐싱하여 성능을 향상시킵니다.
 */
@Configuration
@EnableCaching
public class NotificationCacheConfig {

    public static final String UNREAD_COUNT_CACHE_NAME = "unreadNotificationCount";
    public static final long CACHE_EXPIRE_MINUTES = 5;
    public static final long CACHE_MAX_SIZE = 10000;

    /**
     * 읽지 않은 알림 개수 캐시용 키 생성
     * @param userId 사용자 ID
     * @return 캐시 키
     */
    public static String generateUnreadCountCacheKey(Long userId) {
        return "user:" + userId;
    }

    @Bean
    public CacheManager notificationCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(UNREAD_COUNT_CACHE_NAME);
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES)
                .maximumSize(CACHE_MAX_SIZE));
        return cacheManager;
    }
}
