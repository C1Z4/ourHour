package com.ourhour.global.common.enums;

import java.util.Arrays;

public enum ImageContentType {
    JPEG("image/jpeg", "jpg"),
    JPG("image/jpg", "jpg"),
    PNG("image/png", "png"),
    GIF("image/gif", "gif"),
    WEBP("image/webp", "webp"),
    SVG("image/svg+xml", "svg");

    private final String mimeType;
    private final String extension;

    ImageContentType(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension;
    }

    // ContentType 문자열에서 확장자 추출
    public static String getExtensionFromContentType(String contentType) {
        if (contentType == null || contentType.isEmpty()) {
            return "jpg";
        }

        return Arrays.stream(values())
                .filter(type -> contentType.toLowerCase().contains(type.mimeType))
                .map(type -> type.extension)
                .findFirst()
                .orElse("jpg");
    }

    // MIME 타입 헤더에서 확장자 추출 (Base64 이미지용)
    public static String getExtensionFromMimeTypeHeader(String mimeTypeHeader) {
        if (mimeTypeHeader == null || mimeTypeHeader.isEmpty()) {
            return "png";
        }

        String lower = mimeTypeHeader.toLowerCase();
        if (lower.contains("png")) return "png";
        if (lower.contains("jpg") || lower.contains("jpeg")) return "jpg";
        if (lower.contains("gif")) return "gif";
        if (lower.contains("svg")) return "svg";
        return "png";
    }

    // MIME 타입 헤더에서 ContentType 추출 (Base64 이미지용)
    public static String getContentTypeFromMimeTypeHeader(String mimeTypeHeader) {
        if (mimeTypeHeader == null || mimeTypeHeader.isEmpty()) {
            return "application/octet-stream";
        }

        String lower = mimeTypeHeader.toLowerCase();
        if (lower.contains("image/png")) return "image/png";
        if (lower.contains("image/jpeg") || lower.contains("jpg")) return "image/jpeg";
        if (lower.contains("image/gif")) return "image/gif";
        if (lower.contains("image/svg+xml") || lower.contains("svg")) return "image/svg+xml";
        return "application/octet-stream";
    }
}
