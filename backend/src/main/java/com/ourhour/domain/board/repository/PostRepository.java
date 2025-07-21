package com.ourhour.domain.board.repository;

import com.ourhour.domain.board.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    @Query("SELECT p " +
            "FROM PostEntity p " +
            "WHERE p.boardEntity.orgEntity.orgId = :orgId " +
            "ORDER BY p.createdAt DESC")
    List<PostEntity> findAllByOrgId(Long orgId);

    @Query("SELECT p FROM PostEntity p " +
            "WHERE p.boardEntity.boardId = :boardId " +
            "AND p.boardEntity.orgEntity.orgId = :orgId " +
            "ORDER BY p.createdAt DESC")
    List<PostEntity> findPostsByBoardAndOrg(Long boardId, Long orgId);
}