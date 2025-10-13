package com.ourhour.global.common.controller;

import com.ourhour.global.common.dto.ImageMetadataDTO;
import com.ourhour.global.common.enums.ImageContentType;
import com.ourhour.global.common.service.ImageCacheEvictionService;
import com.ourhour.global.common.service.ImageCacheService;
import com.ourhour.global.common.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ourhour.global.common.dto.ApiResponse;
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

    public record DeleteImageRequest(String imageUrl) {
    }

    @PostMapping(value = "/presign", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PresignResponse> presign(@RequestBody PresignRequest request) {

        // 캐시에서 먼저 확인 (PresignResponse 전체)
        String[] cachedResponse = imageCacheService.getPresignResponse(request.fileName(), request.contentType());
        if (cachedResponse != null) {
            // 캐시된 응답이 있으면 그대로 반환
            String presignedUrl = cachedResponse[0];
            String key = cachedResponse[1];
            String cdnUrl = cachedResponse[2];

            return ResponseEntity.ok(new PresignResponse(presignedUrl, key, cdnUrl));
        }

        // 캐시에 없으면 새로 생성
        String extension = ImageContentType.getExtensionFromContentType(request.contentType());
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

        // 캐시에 저장 (PresignResponse 전체)
        imageCacheService.savePresignResponse(request.fileName(), request.contentType(), presignedUrl, key, cdnUrl);

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

    // 이미지 삭제 API
    @DeleteMapping("/images")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@RequestBody DeleteImageRequest request) {
        try {
            String key = imageService.extractKeyFromCdnUrl(request.imageUrl());

            if (key == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.fail("유효하지 않은 이미지 URL입니다."));
            }

            boolean deleted = imageService.deleteImageFromS3(key);

            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success(null, "이미지가 성공적으로 삭제되었습니다."));
            } else {
                // 이 경우는 이제 발생하지 않지만, 안전을 위해 유지
                return ResponseEntity.ok(ApiResponse.success(null, "이미지가 이미 삭제되었거나 존재하지 않습니다."));
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.fail("이미지 삭제 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}