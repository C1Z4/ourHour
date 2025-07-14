package com.ourhour.global.jwt.annotation;

import com.ourhour.domain.org.enums.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
// 해당 조직의 권한 상태(ROOT_ADMIN > ADMIN > MEMBER)
public @interface OrgAuth {
    Role accessLevel() default Role.MEMBER;
}
