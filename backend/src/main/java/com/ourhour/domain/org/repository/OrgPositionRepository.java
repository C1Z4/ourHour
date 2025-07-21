package com.ourhour.domain.org.repository;


import com.ourhour.domain.org.entity.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrgPositionRepository extends JpaRepository<PositionEntity, Long> {

}