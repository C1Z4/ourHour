package com.ourhour.domain.org.repository;

import com.ourhour.domain.org.entity.OrgEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface OrgRepository extends JpaRepository<OrgEntity, Long> {

}
