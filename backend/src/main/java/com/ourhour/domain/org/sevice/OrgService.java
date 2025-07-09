package com.ourhour.domain.org.sevice;

import com.ourhour.domain.org.dto.OrgReqDTO;
import com.ourhour.domain.org.dto.OrgResDTO;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.mapper.OrgMapper;
import com.ourhour.domain.org.repository.OrgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrgService {

    private final OrgRepository orgRepository;
    private final OrgMapper orgMapper;

    @Transactional
    public OrgResDTO registerOrg(OrgReqDTO orgReqDTO) {

        // OrgReqDTO에서 OrgEntity로 변환
        OrgEntity orgReqEntity = orgMapper.toOrgEntity(orgReqDTO);

        // 데이터베이스에 등록
        OrgEntity orgEntity = orgRepository.save(orgReqEntity);

        // 응답 DTO로 변환
        OrgResDTO orgResDTO = orgMapper.toOrgResDTO(orgEntity);

        return orgResDTO;
    }
}
