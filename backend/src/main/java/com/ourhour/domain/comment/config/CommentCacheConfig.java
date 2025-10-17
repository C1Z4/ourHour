package com.ourhour.domain.comment.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Comment 도메인 캐시 설정
 * 댓글 목록 조회 결과를 캐싱하여 성능을 향상시킵니다.
 */
@Configuration
@EnableCaching
public class CommentCacheConfig {

    public static final String COMMENT_CACHE_NAME = "comments";
    public static final long CACHE_EXPIRE_MINUTES = 10;
    public static final long CACHE_MAX_SIZE = 1000;

    /**
     * 댓글 캐시용 키 생성
     * @param postId 게시글 ID (nullable)
     * @param issueId 이슈 ID (nullable)
     * @return 캐시 키
     */
    public static String generateCacheKey(Long postId, Long issueId) {
        if (postId != null) {
            return "post:" + postId;
        } else if (issueId != null) {
            return "issue:" + issueId;
        }
        throw new IllegalArgumentException("Either postId or issueId must be provided");
    }

    /**
     * 댓글 캐시용 키 패턴 생성 (전체 삭제용)
     * @param postId 게시글 ID (nullable)
     * @param issueId 이슈 ID (nullable)
     * @return 캐시 키 패턴
     */
    public static String generateCacheKeyPattern(Long postId, Long issueId) {
        return generateCacheKey(postId, issueId) + "*";
    }

    @Bean
    public CacheManager commentCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(COMMENT_CACHE_NAME);
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES)
                .maximumSize(CACHE_MAX_SIZE));
        return cacheManager;
    }
}
