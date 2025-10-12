package com.ourhour.domain.project.sync;

import org.springframework.stereotype.Component;
import com.ourhour.domain.project.entity.MilestoneEntity;

@Component
public class MilestoneProjectIdExtractor implements ProjectIdExtractor<MilestoneEntity> {

    @Override
    public Long extractProjectId(MilestoneEntity entity) {
        return entity.getProjectEntity().getProjectId();
    }

    @Override
    public Class<MilestoneEntity> getSupportedEntityType() {
        return MilestoneEntity.class;
    }
}
