package com.ourhour.global.common.service;

import com.ourhour.global.common.dto.ImageMetadataDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ImageCacheServiceTest {

    @Autowired
    private ImageCacheService imageCacheService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    void saveAndRetrieveImageMetadata() {
        ImageMetadataDTO metadata = ImageMetadataDTO.builder()
                .key("test/image1.jpg")
                .fileName("image1.jpg")
                .cdnUrl("https://cdn.example.com/test/image1.jpg")
                .contentType("image/jpeg")
                .size(1024L)
                .uploadTime(LocalDateTime.now())
                .accessCount(1)
                .build();

        imageCacheService.saveImageMetadata(metadata);

        ImageMetadataDTO retrieved = imageCacheService.getImageMetadata("test/image1.jpg");
        
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getKey()).isEqualTo("test/image1.jpg");
        assertThat(retrieved.getFileName()).isEqualTo("image1.jpg");
        assertThat(retrieved.getCdnUrl()).isEqualTo("https://cdn.example.com/test/image1.jpg");
    }

    @Test
    void incrementAccessCount() {
        ImageMetadataDTO metadata = ImageMetadataDTO.builder()
                .key("test/image2.jpg")
                .fileName("image2.jpg")
                .cdnUrl("https://cdn.example.com/test/image2.jpg")
                .contentType("image/jpeg")
                .size(2048L)
                .uploadTime(LocalDateTime.now())
                .accessCount(1)
                .build();

        imageCacheService.saveImageMetadata(metadata);
        imageCacheService.incrementAccessCount("test/image2.jpg");
        
        Integer accessCount = imageCacheService.getAccessCount("test/image2.jpg");
        assertThat(accessCount).isGreaterThan(1);
    }

    @Test
    void cachePresignedUrl() {
        String fileName = "test.jpg";
        String contentType = "image/jpeg";
        String presignedUrl = "https://s3.amazonaws.com/bucket/presigned-url";

        imageCacheService.savePresignedUrl(fileName, contentType, presignedUrl);
        
        String cached = imageCacheService.getPresignedUrl(fileName, contentType);
        assertThat(cached).isEqualTo(presignedUrl);
    }

    @Test
    void evictCache() {
        ImageMetadataDTO metadata = ImageMetadataDTO.builder()
                .key("test/image3.jpg")
                .fileName("image3.jpg")
                .cdnUrl("https://cdn.example.com/test/image3.jpg")
                .contentType("image/jpeg")
                .size(512L)
                .uploadTime(LocalDateTime.now())
                .accessCount(1)
                .build();

        imageCacheService.saveImageMetadata(metadata);
        assertThat(imageCacheService.getImageMetadata("test/image3.jpg")).isNotNull();

        imageCacheService.evictImageMetadata("test/image3.jpg");
        assertThat(imageCacheService.getImageMetadata("test/image3.jpg")).isNull();
    }
}