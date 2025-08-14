package com.ourhour.domain.project.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ourhour.domain.project.enums.SyncOperation;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GitHubSync {
    SyncOperation operation() default SyncOperation.CREATE;

    String entityParam() default ""; // 동기화할 엔티티 파라미터명
}
