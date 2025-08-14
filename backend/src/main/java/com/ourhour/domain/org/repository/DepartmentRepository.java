package com.ourhour.domain.org.repository;

import com.ourhour.domain.org.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long> {
    
    Optional<DepartmentEntity> findByName(String name);
    
    @Query("SELECT DISTINCT d FROM DepartmentEntity d " +
           "JOIN d.orgParticipantMemberEntityList opm " +
           "WHERE opm.orgEntity.orgId = :orgId")
    List<DepartmentEntity> findByOrgId(@Param("orgId") Long orgId);
    
    boolean existsByName(String name);
}