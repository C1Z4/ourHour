package com.ourhour.domain.org.repository;

import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrgParticipantMemberRepository extends JpaRepository<OrgParticipantMemberEntity, Long> {
}
