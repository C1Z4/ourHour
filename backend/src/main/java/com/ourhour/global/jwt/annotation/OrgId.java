package com.ourhour.global.jwt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
// 해당 조직의 소속 상태 (true : 권한 확인 필요, false: 접근 권한 없음)
public @interface OrgId {
}
