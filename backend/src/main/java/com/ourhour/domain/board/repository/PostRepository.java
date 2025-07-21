package com.ourhour.domain.board.repository;

import com.ourhour.domain.board.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    List<PostEntity> findByBoardEntity_BoardIdOrderByPostIdDesc(Long boardId);

}
