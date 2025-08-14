package com.ourhour.global.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageCacheEvictionService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ImageCacheService imageCacheService;

    @Value("${app.cache.image.max-size:1000}")
    private int maxCacheSize;

    private static final String IMAGE_METADATA_PREFIX = "image:metadata:*";

    // 가장 오래된 이미지 정리
    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    public void evictLeastUsedImages() {
        try {
            Set<String> metadataKeys = redisTemplate.keys(IMAGE_METADATA_PREFIX);
            if (metadataKeys == null || metadataKeys.size() <= maxCacheSize) {
                return;
            }

            log.info("캐시 크기 초과 감지. 현재: {}, 최대: {}", metadataKeys.size(), maxCacheSize);
            
            int toEvict = metadataKeys.size() - maxCacheSize;
            
            metadataKeys.stream()
                    .map(key -> key.replace("image:metadata:", ""))
                    .sorted((key1, key2) -> {
                        Integer count1 = imageCacheService.getAccessCount(key1);
                        Integer count2 = imageCacheService.getAccessCount(key2);
                        return count1.compareTo(count2);
                    })
                    .limit(toEvict)
                    .forEach(key -> {
                        imageCacheService.evictImageMetadata(key);
                        log.debug("캐시에서 제거된 이미지: {}", key);
                    });

            log.info("{}개의 이미지 메타데이터가 캐시에서 제거되었습니다", toEvict);
            
        } catch (Exception e) {
            log.error("이미지 캐시 정리 중 오류 발생", e);
        }
    }

    // 만료된 Presigned URL 정리
    @Scheduled(fixedRate = 1800000) // 30분마다 실행  
    public void cleanupExpiredPresignedUrls() {
        try {
            Set<String> presignKeys = redisTemplate.keys("image:presign:*");
            if (presignKeys == null) {
                return;
            }

            int cleaned = 0;
            for (String key : presignKeys) {
                if (redisTemplate.getExpire(key) <= 0) {
                    redisTemplate.delete(key);
                    cleaned++;
                }
            }
            
            if (cleaned > 0) {
                log.info("만료된 Presigned URL {}개가 정리되었습니다", cleaned);
            }
            
        } catch (Exception e) {
            log.error("Presigned URL 정리 중 오류 발생", e);
        }
    }

    // 모든 캐시 삭제
    public void evictAll() {
        try {
            Set<String> allKeys = redisTemplate.keys("image:*");
            if (allKeys != null && !allKeys.isEmpty()) {
                redisTemplate.delete(allKeys);
                log.info("모든 이미지 캐시가 삭제되었습니다. 삭제된 키 수: {}", allKeys.size());
            }
        } catch (Exception e) {
            log.error("전체 이미지 캐시 삭제 중 오류 발생", e);
        }
    }

    // 캐시 크기 조회
    public long getCacheSize() {
        Set<String> keys = redisTemplate.keys(IMAGE_METADATA_PREFIX);
        return keys != null ? keys.size() : 0;
    }   
}