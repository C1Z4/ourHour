package com.ourhour.global.common.service;

import com.ourhour.global.common.dto.ImageMetadataDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.cache.image.ttl:3600}")
    private long imageCacheTtl;

    @Value("${app.cache.presign.ttl:600}")
    private long presignCacheTtl;

    private static final String IMAGE_METADATA_PREFIX = "image:metadata:";
    private static final String PRESIGN_URL_PREFIX = "image:presign:";
    private static final String ACCESS_COUNT_PREFIX = "image:access:";
    private static final String CACHE_DELIMITER = "|";
    private static final int PRESIGN_RESPONSE_PARTS = 3;

    @Cacheable(value = "imageMetadata", key = "#key")
    public ImageMetadataDTO getImageMetadata(String key) {
        try {
            String redisKey = IMAGE_METADATA_PREFIX + key;
            return (ImageMetadataDTO) redisTemplate.opsForValue().get(redisKey);
        } catch (Exception e) {
            log.warn("Redis 연결 오류로 인해 캐시에서 이미지 메타데이터를 가져올 수 없습니다: {}", e.getMessage());
            return null;
        }
    }

    @CachePut(value = "imageMetadata", key = "#metadata.key")
    public ImageMetadataDTO saveImageMetadata(ImageMetadataDTO metadata) {
        try {
            String redisKey = IMAGE_METADATA_PREFIX + metadata.getKey();
            redisTemplate.opsForValue().set(redisKey, metadata, imageCacheTtl, TimeUnit.SECONDS);
            log.debug("이미지 메타데이터 캐시 저장: {}", metadata.getKey());
        } catch (Exception e) {
            log.warn("Redis 연결 오류로 인해 이미지 메타데이터를 캐시에 저장할 수 없습니다: {}", e.getMessage());
        }
        return metadata;
    }

    public String getPresignedUrl(String fileName, String contentType) {
        try {
            String cacheKey = PRESIGN_URL_PREFIX + fileName + ":" + contentType;
            return (String) redisTemplate.opsForValue().get(cacheKey);
        } catch (Exception e) {
            log.warn("Redis 연결 오류로 인해 Presigned URL을 캐시에서 가져올 수 없습니다: {}", e.getMessage());
            return null;
        }
    }

    public void savePresignedUrl(String fileName, String contentType, String presignedUrl) {
        try {
            String cacheKey = PRESIGN_URL_PREFIX + fileName + ":" + contentType;
            redisTemplate.opsForValue().set(cacheKey, presignedUrl, presignCacheTtl, TimeUnit.SECONDS);
            log.debug("Presigned URL 캐시 저장: {}", fileName);
        } catch (Exception e) {
            log.warn("Redis 연결 오류로 인해 Presigned URL을 캐시에 저장할 수 없습니다: {}", e.getMessage());
        }
    }

    // PresignResponse 전체를 캐시하는 메서드 추가
    public void savePresignResponse(String fileName, String contentType, String presignedUrl, String key,
            String cdnUrl) {
        try {
            String cacheKey = PRESIGN_URL_PREFIX + fileName + ":" + contentType;
            // PresignResponse 객체를 JSON으로 직렬화하여 저장
            String cacheValue = presignedUrl + CACHE_DELIMITER + key + CACHE_DELIMITER + cdnUrl;
            redisTemplate.opsForValue().set(cacheKey, cacheValue, presignCacheTtl, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Redis 연결 오류로 인해 PresignResponse를 캐시에 저장할 수 없습니다: {}", e.getMessage());
        }
    }

    // PresignResponse 전체를 캐시에서 가져오는 메서드 추가
    public String[] getPresignResponse(String fileName, String contentType) {
        try {
            String cacheKey = PRESIGN_URL_PREFIX + fileName + ":" + contentType;
            String cachedValue = (String) redisTemplate.opsForValue().get(cacheKey);
            if (cachedValue != null) {
                String[] parts = cachedValue.split("\\" + CACHE_DELIMITER);
                if (parts.length == PRESIGN_RESPONSE_PARTS) {
                    return parts;
                }
            }
            return null;
        } catch (Exception e) {
            log.warn("Redis 연결 오류로 인해 PresignResponse를 캐시에서 가져올 수 없습니다: {}", e.getMessage());
            return null;
        }
    }

    public void incrementAccessCount(String key) {
        try {
            String countKey = ACCESS_COUNT_PREFIX + key;
            redisTemplate.opsForValue().increment(countKey);
            redisTemplate.expire(countKey, imageCacheTtl, TimeUnit.SECONDS);

            ImageMetadataDTO metadata = getImageMetadata(key);
            if (metadata != null) {
                Integer currentCount = (Integer) redisTemplate.opsForValue().get(countKey);
                metadata.setAccessCount(currentCount != null ? currentCount : 1);
                saveImageMetadata(metadata);
            }
        } catch (Exception e) {
            log.warn("Redis 연결 오류로 인해 접근 횟수를 증가시킬 수 없습니다: {}", e.getMessage());
        }
    }

    public Integer getAccessCount(String key) {
        try {
            String countKey = ACCESS_COUNT_PREFIX + key;
            Integer count = (Integer) redisTemplate.opsForValue().get(countKey);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.warn("Redis 연결 오류로 인해 접근 횟수를 가져올 수 없습니다: {}", e.getMessage());
            return 0;
        }
    }

    @CacheEvict(value = "imageMetadata", key = "#key")
    public void evictImageMetadata(String key) {
        try {
            String redisKey = IMAGE_METADATA_PREFIX + key;
            String countKey = ACCESS_COUNT_PREFIX + key;

            redisTemplate.delete(redisKey);
            redisTemplate.delete(countKey);
            log.debug("이미지 메타데이터 캐시 삭제: {}", key);
        } catch (Exception e) {
            log.warn("Redis 연결 오류로 인해 이미지 메타데이터 캐시를 삭제할 수 없습니다: {}", e.getMessage());
        }
    }

    public void evictPresignedUrl(String fileName, String contentType) {
        try {
            String cacheKey = PRESIGN_URL_PREFIX + fileName + ":" + contentType;
            redisTemplate.delete(cacheKey);
            log.debug("Presigned URL 캐시 삭제: {}", fileName);
        } catch (Exception e) {
            log.warn("Redis 연결 오류로 인해 Presigned URL 캐시를 삭제할 수 없습니다: {}", e.getMessage());
        }
    }

    public ImageMetadataDTO createImageMetadata(String key, String fileName, String cdnUrl, String contentType,
            Long size) {
        return ImageMetadataDTO.builder()
                .key(key)
                .fileName(fileName)
                .cdnUrl(cdnUrl)
                .contentType(contentType)
                .size(size)
                .uploadTime(LocalDateTime.now())
                .accessCount(1)
                .build();
    }
}