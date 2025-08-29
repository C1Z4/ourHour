package com.ourhour.global.util;

import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

public class AsyncUtil {
    private static final SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();

    public static DelegatingSecurityContextExecutor getExecutor() {
        SecurityContext context = SecurityContextHolder.getContext();
        return new DelegatingSecurityContextExecutor(executor, context);
    }
}
