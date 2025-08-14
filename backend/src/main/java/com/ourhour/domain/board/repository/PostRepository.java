package com.ourhour.domain.board.repository;

import com.ourhour.domain.board.entity.PostEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    @Query("SELECT p " +
            "FROM PostEntity p " +
            "WHERE p.boardEntity.orgEntity.orgId = :orgId " +
            "ORDER BY p.createdAt DESC")
    Page<PostEntity> findAllByOrgId(Long orgId, Pageable pageable);

    @Query("SELECT p FROM PostEntity p " +
            "WHERE p.boardEntity.boardId = :boardId " +
            "AND p.boardEntity.orgEntity.orgId = :orgId " +
            "ORDER BY p.createdAt DESC")
    Page<PostEntity> findPostsByBoardAndOrg(Long boardId, Long orgId, Pageable pageable);
}