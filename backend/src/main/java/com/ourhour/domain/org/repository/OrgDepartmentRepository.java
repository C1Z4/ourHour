package com.ourhour.domain.org.repository;

import com.ourhour.domain.org.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrgDepartmentRepository extends JpaRepository<DepartmentEntity, Long> {

}