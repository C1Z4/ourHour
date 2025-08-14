package com.ourhour.domain.org.repository;

import com.ourhour.domain.org.entity.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<PositionEntity, Long> {
    
    Optional<PositionEntity> findByName(String name);
    
    @Query("SELECT DISTINCT p FROM PositionEntity p " +
           "JOIN p.orgParticipantMemberEntityList opm " +
           "WHERE opm.orgEntity.orgId = :orgId")
    List<PositionEntity> findByOrgId(@Param("orgId") Long orgId);
    
    boolean existsByName(String name);
}