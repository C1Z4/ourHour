package com.ourhour.global.common.controller;

import com.ourhour.global.common.dto.ImageMetadataDTO;
import com.ourhour.global.common.service.ImageCacheEvictionService;
import com.ourhour.global.common.service.ImageCacheService;
import com.ourhour.global.common.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
@Validated
public class StorageController {

    private final S3Presigner s3Presigner;
    private final ImageService imageService;
    private final ImageCacheService imageCacheService;
    private final ImageCacheEvictionService cacheEvictionService;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${app.upload.prefix:images}")
    private String uploadPrefix;

    public record PresignRequest(String fileName, String contentType) {
    }

    public record PresignResponse(String url, String key, String cdnUrl) {
    }

    @PostMapping(value = "/presign", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PresignResponse> presign(@RequestBody PresignRequest request) {

        // 캐시에서 먼저 확인   
        String cachedUrl = imageCacheService.getPresignedUrl(request.fileName(), request.contentType());
        if (cachedUrl != null) {
            return ResponseEntity.ok(new PresignResponse(cachedUrl, "", ""));
        }

        // 캐시에 없으면 새로 생성
        String extension = getExtensionFromContentType(request.contentType());
        String safeFileName = UUID.randomUUID() + "." + extension;
        String key = uploadPrefix + "/" + safeFileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(request.contentType())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);
        String presignedUrl = presigned.url().toString();

        String cdnUrl = imageService.buildCdnUrl(key);

        // 캐시에 저장
        imageCacheService.savePresignedUrl(request.fileName(), request.contentType(), presignedUrl);

        return ResponseEntity.ok(new PresignResponse(presignedUrl, key, cdnUrl));
    }

    // 이미지 메타데이터 조회
    @GetMapping("/metadata/{key}")
    public ResponseEntity<ImageMetadataDTO> getImageMetadata(@PathVariable String key) {
        ImageMetadataDTO metadata = imageService.getImageMetadata(key);
        if (metadata == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(metadata);
    }

    // 캐시 삭제
    @DeleteMapping("/cache/{key}")
    public ResponseEntity<Void> evictCache(@PathVariable String key) {
        imageService.deleteImage(key);
        return ResponseEntity.ok().build();
    }

    // 모든 캐시 삭제
    @DeleteMapping("/cache/all")
    public ResponseEntity<Void> evictAllCache() {
        cacheEvictionService.evictAll();
        return ResponseEntity.ok().build();
    }

    // 캐시 크기 조회
    @GetMapping("/cache/size")
    public ResponseEntity<Long> getCacheSize() {
        long size = cacheEvictionService.getCacheSize();
        return ResponseEntity.ok(size);
    }

    // 컨텐트 타입에 따라 확장자 반환
    private String getExtensionFromContentType(String contentType) {
        switch (contentType.toLowerCase()) {
            case "image/jpeg":
            case "image/jpg":
                return "jpg";
            case "image/png":
                return "png";
            case "image/gif":
                return "gif";
            case "image/webp":
                return "webp";
            default:
                return "jpg";
        }
    }
}