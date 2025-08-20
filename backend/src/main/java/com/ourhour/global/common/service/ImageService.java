package com.ourhour.global.common.service;

import com.ourhour.global.common.dto.ImageMetadataDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Client s3Client;
    private final ImageCacheService imageCacheService;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${app.cdn.domain}")
    private String cdnDomain;

    @Value("${app.upload.prefix:images}")
    private String uploadPrefix;

    @Value("${app.s3.delete.enabled:true}")
    private boolean s3DeleteEnabled;

    public String saveBase64Image(String base64Data) {
        try {
            String[] parts = base64Data.split(",", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException("잘못된 Base64 형식입니다");
            }

            String mimeTypeHeader = parts[0];
            String imageData = parts[1];

            String extension = getExtensionFromMimeType(mimeTypeHeader);
            String contentType = getContentTypeFromMimeType(mimeTypeHeader);

            String fileName = UUID.randomUUID() + "." + extension;
            String key = uploadPrefix + "/" + fileName;

            byte[] decodedBytes = Base64.getDecoder().decode(imageData.getBytes(StandardCharsets.UTF_8));

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .cacheControl("public, max-age=31536000, immutable")
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(decodedBytes));

            String cdnUrl = buildCdnUrl(key);

            ImageMetadataDTO metadata = imageCacheService.createImageMetadata(
                    key, fileName, cdnUrl, contentType, (long) decodedBytes.length);
            imageCacheService.saveImageMetadata(metadata);

            return cdnUrl;

        } catch (Exception e) {
            throw new RuntimeException("이미지 저장 중 오류가 발생했습니다", e);
        }
    }

    private String getExtensionFromMimeType(String mimeType) {
        String lower = mimeType.toLowerCase();
        if (lower.contains("png"))
            return "png";
        if (lower.contains("jpg") || lower.contains("jpeg"))
            return "jpg";
        if (lower.contains("gif"))
            return "gif";
        if (lower.contains("svg"))
            return "svg";
        return "png";
    }

    private String getContentTypeFromMimeType(String mimeType) {
        String lower = mimeType.toLowerCase();
        if (lower.contains("image/png"))
            return "image/png";
        if (lower.contains("image/jpeg") || lower.contains("jpg"))
            return "image/jpeg";
        if (lower.contains("image/gif"))
            return "image/gif";
        if (lower.contains("image/svg+xml") || lower.contains("svg"))
            return "image/svg+xml";
        return "application/octet-stream";
    }

    public String buildCdnUrl(String key) {
        String base = cdnDomain.endsWith("/") ? cdnDomain.substring(0, cdnDomain.length() - 1) : cdnDomain;
        String cdnUrl = base + "/" + key;
        return cdnUrl;
    }

    public ImageMetadataDTO getImageMetadata(String key) {
        ImageMetadataDTO metadata = imageCacheService.getImageMetadata(key);
        if (metadata != null) {
            imageCacheService.incrementAccessCount(key);
        }
        return metadata;
    }

    public void deleteImage(String key) {
        imageCacheService.evictImageMetadata(key);
    }

    public boolean deleteImageFromS3(String key) {
        try {
            // 로컬 개발환경에서는 S3 삭제를 건너뛰고 성공으로 처리
            if (!s3DeleteEnabled) {
                imageCacheService.evictImageMetadata(key);
                return true;
            }

            // S3에서 파일 존재 여부 확인
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.headObject(headObjectRequest);

            // 파일이 존재하면 삭제
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

            // 캐시에서도 삭제
            imageCacheService.evictImageMetadata(key);

            return true;
        } catch (NoSuchKeyException e) {
            // 파일이 존재하지 않음 - 이미 삭제된 것으로 간주하고 캐시만 정리
            imageCacheService.evictImageMetadata(key);
            return true; // 파일이 없어도 삭제 성공으로 처리
        } catch (Exception e) {
            throw new RuntimeException("이미지 삭제 중 오류가 발생했습니다", e);
        }
    }

    // 이미지 URL에서 key 추출
    public String extractKeyFromCdnUrl(String cdnUrl) {
        if (cdnUrl == null || cdnUrl.isEmpty()) {
            return null;
        }

        // CDN 도메인이 설정되어 있고 유효한 경우
        if (cdnDomain != null && !cdnDomain.isEmpty() && !cdnDomain.equals("${CDN_DOMAIN}")) {
            String base = cdnDomain.endsWith("/") ? cdnDomain.substring(0, cdnDomain.length() - 1) : cdnDomain;

            if (cdnUrl.startsWith(base + "/")) {
                String key = cdnUrl.substring(base.length() + 1);
                return key;
            }
        }

        // CloudFront URL에서 직접 키 추출 시도
        if (cdnUrl.contains("cloudfront.net/")) {
            String[] parts = cdnUrl.split("cloudfront.net/", 2);
            if (parts.length == 2) {
                String key = parts[1];
                return key;
            }
        }

        // S3 URL에서 직접 키 추출 시도
        if (cdnUrl.contains("amazonaws.com/")) {
            String[] parts = cdnUrl.split("amazonaws.com/", 2);
            if (parts.length == 2) {
                String key = parts[1];
                return key;
            }
        }

        // 패턴 매칭으로 images/ 이후의 부분 추출
        if (cdnUrl.contains("/" + uploadPrefix + "/")) {
            int index = cdnUrl.indexOf("/" + uploadPrefix + "/");
            String key = cdnUrl.substring(index + 1);
            return key;
        }

        return null;
    }
}
