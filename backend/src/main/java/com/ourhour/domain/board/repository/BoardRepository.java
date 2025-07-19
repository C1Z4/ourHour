package com.ourhour.domain.board.repository;

import com.ourhour.domain.board.dto.BoardDTO;
import com.ourhour.domain.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

    List<BoardEntity> findAllByOrgEntity_OrgId(Long orgId);


}
