package com.ourhour.domain.project.sync;

import org.springframework.stereotype.Component;
import com.ourhour.domain.project.entity.IssueEntity;

@Component
public class IssueProjectIdExtractor implements ProjectIdExtractor<IssueEntity> {

    @Override
    public Long extractProjectId(IssueEntity entity) {
        return entity.getProjectEntity().getProjectId();
    }

    @Override
    public Class<IssueEntity> getSupportedEntityType() {
        return IssueEntity.class;
    }
}
