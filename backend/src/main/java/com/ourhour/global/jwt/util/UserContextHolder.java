package com.ourhour.global.jwt.util;

import com.ourhour.global.jwt.dto.Claims;

// 현재 요청(Request) 내에서만 유효한 사용자 인증 정보(Claims)를 저장하는 ThreadLocal 기반 저장소
public class UserContextHolder {

    private static final ThreadLocal<Claims> context = new ThreadLocal<>();

    public static void set(Claims claims) {
        context.set(claims);
    }

    public static Claims get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }

}
