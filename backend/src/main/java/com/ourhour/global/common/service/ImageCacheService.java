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

    @Cacheable(value = "imageMetadata", key = "#key")
    public ImageMetadataDTO getImageMetadata(String key) {
        String redisKey = IMAGE_METADATA_PREFIX + key;
        return (ImageMetadataDTO) redisTemplate.opsForValue().get(redisKey);
    }

    @CachePut(value = "imageMetadata", key = "#metadata.key")
    public ImageMetadataDTO saveImageMetadata(ImageMetadataDTO metadata) {
        String redisKey = IMAGE_METADATA_PREFIX + metadata.getKey();
        redisTemplate.opsForValue().set(redisKey, metadata, imageCacheTtl, TimeUnit.SECONDS);
        log.debug("이미지 메타데이터 캐시 저장: {}", metadata.getKey());
        return metadata;
    }

    public String getPresignedUrl(String fileName, String contentType) {
        String cacheKey = PRESIGN_URL_PREFIX + fileName + ":" + contentType;
        return (String) redisTemplate.opsForValue().get(cacheKey);
    }

    public void savePresignedUrl(String fileName, String contentType, String presignedUrl) {
        String cacheKey = PRESIGN_URL_PREFIX + fileName + ":" + contentType;
        redisTemplate.opsForValue().set(cacheKey, presignedUrl, presignCacheTtl, TimeUnit.SECONDS);
        log.debug("Presigned URL 캐시 저장: {}", fileName);
    }

    public void incrementAccessCount(String key) {
        String countKey = ACCESS_COUNT_PREFIX + key;
        redisTemplate.opsForValue().increment(countKey);
        redisTemplate.expire(countKey, imageCacheTtl, TimeUnit.SECONDS);
        
        ImageMetadataDTO metadata = getImageMetadata(key);
        if (metadata != null) {
            Integer currentCount = (Integer) redisTemplate.opsForValue().get(countKey);
            metadata.setAccessCount(currentCount != null ? currentCount : 1);
            saveImageMetadata(metadata);
        }
    }

    public Integer getAccessCount(String key) {
        String countKey = ACCESS_COUNT_PREFIX + key;
        Integer count = (Integer) redisTemplate.opsForValue().get(countKey);
        return count != null ? count : 0;
    }

    @CacheEvict(value = "imageMetadata", key = "#key")
    public void evictImageMetadata(String key) {
        String redisKey = IMAGE_METADATA_PREFIX + key;
        String countKey = ACCESS_COUNT_PREFIX + key;
        
        redisTemplate.delete(redisKey);
        redisTemplate.delete(countKey);
        log.debug("이미지 메타데이터 캐시 삭제: {}", key);
    }

    public void evictPresignedUrl(String fileName, String contentType) {
        String cacheKey = PRESIGN_URL_PREFIX + fileName + ":" + contentType;
        redisTemplate.delete(cacheKey);
        log.debug("Presigned URL 캐시 삭제: {}", fileName);
    }

    public ImageMetadataDTO createImageMetadata(String key, String fileName, String cdnUrl, String contentType, Long size) {
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