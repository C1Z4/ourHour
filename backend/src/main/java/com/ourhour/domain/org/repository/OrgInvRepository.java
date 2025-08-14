package com.ourhour.domain.org.repository;

import com.ourhour.domain.org.entity.OrgInvEntity;
import com.ourhour.domain.org.enums.InvStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrgInvRepository extends JpaRepository<OrgInvEntity, Long> {
    Optional<OrgInvEntity> findByToken(String token);

    List<OrgInvEntity> findByStatusAndExpiredAtBefore(InvStatus invStatus, LocalDateTime now);

    @Query("""
        select oiv
        from OrgInvEntity oiv
        join fetch oiv.orgInvBatchEntity oivb
        where oivb.batchId in :batchIds
        order by oiv.createdAt desc 
    """)
    List<OrgInvEntity> findAllByBatchIds(@Param("batchIds") List<Long> batchIds);

}
