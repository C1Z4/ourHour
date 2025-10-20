package com.ourhour.domain.project.aspect.strategy;

import com.ourhour.domain.project.entity.GitHubSyncableEntity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Entity 동기화 전략을 관리하는 Registry
 * 새로운 Entity 타입 추가 시, EntitySyncStrategy 구현체만 추가하면 자동으로 등록됨
 */
@Slf4j
@Component
public class EntitySyncStrategyRegistry {

    private final List<EntitySyncStrategy> strategies;

    public EntitySyncStrategyRegistry(List<EntitySyncStrategy> strategies) {
        this.strategies = strategies;
        log.info("EntitySyncStrategyRegistry 초기화 완료 - 등록된 전략: {}", strategies.size());
    }

    /**
     * entityParam 힌트를 기반으로 적절한 전략을 찾아 Entity를 조회
     */
    public GitHubSyncableEntity findEntityById(String entityParam, Long id) {
        if (id == null) {
            return null;
        }

        // entityParam 힌트가 있는 경우, 해당 전략을 우선 사용
        if (entityParam != null && !entityParam.isBlank()) {
            for (EntitySyncStrategy strategy : strategies) {
                if (strategy.supports(entityParam)) {
                    return strategy.findById(id);
                }
            }
        }

        // 힌트가 없거나 찾지 못한 경우, 모든 전략을 순회하며 조회
        for (EntitySyncStrategy strategy : strategies) {
            GitHubSyncableEntity entity = strategy.findById(id);
            if (entity != null) {
                return entity;
            }
        }

        return null;
    }

    /**
     * DTO 객체로부터 Entity 추출
     */
    public GitHubSyncableEntity extractFromDto(Object dto) {
        if (dto == null) {
            return null;
        }

        for (EntitySyncStrategy strategy : strategies) {
            GitHubSyncableEntity entity = strategy.extractFromDto(dto);
            if (entity != null) {
                return entity;
            }
        }

        return null;
    }

    /**
     * JoinPoint와 entityParam 힌트를 기반으로 Entity 조회
     */
    public GitHubSyncableEntity findEntityFromJoinPoint(JoinPoint joinPoint, String entityParam) {
        // entityParam 힌트에 맞는 전략 찾기
        EntitySyncStrategy targetStrategy = null;
        if (entityParam != null && !entityParam.isBlank()) {
            for (EntitySyncStrategy strategy : strategies) {
                if (strategy.supports(entityParam)) {
                    targetStrategy = strategy;
                    break;
                }
            }
        }

        // 전략을 찾았다면 해당 전략으로 ID 추출
        if (targetStrategy != null) {
            Long id = targetStrategy.extractIdFromArgs(joinPoint, entityParam);
            if (id != null) {
                return targetStrategy.findById(id);
            }
        }

        // 전략을 찾지 못했거나 ID를 추출하지 못한 경우, 모든 전략 시도
        for (EntitySyncStrategy strategy : strategies) {
            Long id = strategy.extractIdFromArgs(joinPoint, entityParam);
            if (id != null) {
                GitHubSyncableEntity entity = strategy.findById(id);
                if (entity != null) {
                    return entity;
                }
            }
        }

        return null;
    }
}
