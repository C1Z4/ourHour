package com.ourhour.domain.notification.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RelatedType {
    CHATROOM("chatroom"),
    ISSUE("issue"),
    POST("post");

    private final String value;
}
