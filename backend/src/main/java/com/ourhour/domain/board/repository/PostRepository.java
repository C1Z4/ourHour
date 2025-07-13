package com.ourhour.domain.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ourhour.domain.board.entity.PostEntity;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

}
