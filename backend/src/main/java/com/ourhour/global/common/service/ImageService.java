package com.ourhour.global.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
public class ImageService {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${app.cdn.domain}")
    private String cdnDomain;

    @Value("${app.upload.prefix:images}")
    private String uploadPrefix;

    public ImageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

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

            return buildCdnUrl(key);

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
        return base + "/" + key;
    }
}
