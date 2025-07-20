package com.ourhour.global.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


@Service
public class ImageService {

    @Value("${app.upload.path}")
    private String uploadPath;

    public String saveBase64Image(String base64Data) {
        try {
            // 업로드 디렉토리 생성
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Base64에서 헤더 제거 (data:image/png;base64, 부분)
            String[] parts = base64Data.split(",");
            if (parts.length != 2) {
                throw new IllegalArgumentException("잘못된 Base64 형식입니다");
            }

            String mimeType = parts[0];
            String imageData = parts[1];

            // 파일 확장자 추출
            String extension = getExtensionFromMimeType(mimeType);

            // 고유 파일명 생성
            String fileName = UUID.randomUUID().toString() + "." + extension;
            String filePath = uploadPath + "/" + fileName;

            // Base64 디코딩 후 파일 저장
            byte[] decodedBytes = Base64.getDecoder().decode(imageData);
            Files.write(Paths.get(filePath), decodedBytes);

            // 저장된 파일의 URL 반환
            return "/images/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("이미지 저장 중 오류가 발생했습니다", e);
        }
    }

    private String getExtensionFromMimeType(String mimeType) {
        if (mimeType.contains("png")) return "png";
        if (mimeType.contains("jpg") || mimeType.contains("jpeg")) return "jpg";
        if (mimeType.contains("gif")) return "gif";
        if (mimeType.contains("svg")) return "svg";
        return "png"; // 기본값
    }
}

