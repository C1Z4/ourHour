package com.ourhour.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SSEEventDTO {
    private String type;
    private Object data;
}