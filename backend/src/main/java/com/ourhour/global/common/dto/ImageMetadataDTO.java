package com.ourhour.global.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageMetadataDTO {
    private String key;
    private String fileName;
    private String cdnUrl;
    private String contentType;
    private Long size;
    private LocalDateTime uploadTime;
    private Integer accessCount;
}