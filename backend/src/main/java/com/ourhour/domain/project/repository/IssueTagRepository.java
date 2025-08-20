package com.ourhour.domain.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ourhour.domain.project.entity.IssueTagEntity;

@Repository
public interface IssueTagRepository extends JpaRepository<IssueTagEntity, Long> {

    List<IssueTagEntity> findByProjectEntity_ProjectId(Long projectId);

    Optional<IssueTagEntity> findByProjectEntity_ProjectIdAndName(Long projectId, String name);
}
