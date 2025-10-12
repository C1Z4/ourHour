package com.ourhour.domain.project.sync;

import org.springframework.stereotype.Component;
import com.ourhour.domain.comment.entity.CommentEntity;
import com.ourhour.domain.project.entity.IssueEntity;

@Component
public class CommentProjectIdExtractor implements ProjectIdExtractor<CommentEntity> {

    @Override
    public Long extractProjectId(CommentEntity entity) {
        IssueEntity issue = entity.getIssueEntity();
        return issue != null ? issue.getProjectEntity().getProjectId() : null;
    }

    @Override
    public Class<CommentEntity> getSupportedEntityType() {
        return CommentEntity.class;
    }
}
