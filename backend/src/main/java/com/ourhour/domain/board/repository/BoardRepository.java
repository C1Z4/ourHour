package com.ourhour.domain.board.repository;


import com.ourhour.domain.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

    // 게시판 이름으로 검색하는 기능 추가
    List<BoardEntity> findByName(String name);

    // 특정 단어가 포함된 게시판 이름으로 검색 (JPQL 사용)
    @Query("SELECT b FROM BoardEntity b WHERE b.name LIKE %:keyword%")
    List<BoardEntity> findByNameContaining(@Param("keyword") String keyword);


}
