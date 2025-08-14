package com.ourhour.domain.org.repository;

import com.ourhour.domain.org.entity.DepartmentEntity;
import com.ourhour.domain.org.entity.OrgEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long> {
    
    Optional<DepartmentEntity> findByName(String name);
    
    List<DepartmentEntity> findByOrgEntity(OrgEntity orgEntity);
    
    boolean existsByName(String name);
    
    boolean existsByNameAndOrgEntity(String name, OrgEntity orgEntity);
}