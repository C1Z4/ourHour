package com.ourhour.global.jwt.mapper;

import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.global.jwt.dto.OrgAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OrgAuthorityMapper {

    // JWT 파싱 시 Map 리스트 → OrgAuthority 리스트로 변환
    public List<OrgAuthority> mapListToOrgAuthorityListHelper(List<Map<String, Object>> mapList) {
        if (mapList == null) return null;

        return mapList.stream()
                .map(
                        map -> OrgAuthority.builder()
                                .orgId(((Number) map.get("orgId")).longValue())
                                .memberId(((Number) map.get("memberId")).longValue())
                                .role(Role.valueOf((String)map.get("role")))
                                .build()
                )
                .collect(Collectors.toList());
    }

    // JWT 발급 시 Entity → OrgAuthority 리스트로 변환
    public List<OrgAuthority> toOrgAuthority (List<OrgParticipantMemberEntity> orgParticipantMemberEntityList) {
        return orgParticipantMemberEntityList.stream()
                .map(memberOrg -> OrgAuthority.builder()
                        .orgId(memberOrg.getOrgEntity().getOrgId())
                        .memberId(memberOrg.getMemberEntity().getMemberId())
                        .role(memberOrg.getRole())
                        .build()
                ).collect(Collectors.toList());
    }

}
