package com.ourhour.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 서버 CPU 코어 기준으로 코어 풀 수 설정
        int core = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(core);        // 기본 스레드 수
        executor.setMaxPoolSize(core * 4);     // 최대 스레드 수
        executor.setQueueCapacity(5000);       // 큐 용량
        executor.setThreadNamePrefix("Async-Mail-");

        // 큐가 꽉 차면 호출 스레드가 직접 실행 (TaskRejectedException 방지)
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }
}
