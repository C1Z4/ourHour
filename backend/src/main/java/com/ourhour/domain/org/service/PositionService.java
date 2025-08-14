package com.ourhour.domain.org.service;

import com.ourhour.domain.org.dto.PositionReqDTO;
import com.ourhour.domain.org.dto.PositionResDTO;
import com.ourhour.domain.org.entity.PositionEntity;
import com.ourhour.domain.org.exception.OrgException;
import com.ourhour.domain.org.repository.PositionRepository;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PositionService {

    private final PositionRepository positionRepository;
    private final OrgParticipantMemberRepository orgParticipantMemberRepository;

    @Transactional
    public PositionResDTO createPosition(PositionReqDTO positionReqDTO) {
        if (positionRepository.existsByName(positionReqDTO.getName())) {
            throw OrgException.positionNameDuplicateException();
        }

        PositionEntity position = PositionEntity.builder()
                .name(positionReqDTO.getName())
                .build();

        PositionEntity savedPosition = positionRepository.save(position);

        return PositionResDTO.builder()
                .positionId(savedPosition.getPositionId())
                .name(savedPosition.getName())
                .memberCount(0L)
                .build();
    }

    public List<PositionResDTO> getAllPositions() {
        return positionRepository.findAll().stream()
                .map(position -> PositionResDTO.builder()
                        .positionId(position.getPositionId())
                        .name(position.getName())
                        .build())
                .collect(Collectors.toList());
    }

    public List<PositionResDTO> getPositionsByOrg(Long orgId) {
        return positionRepository.findByOrgId(orgId).stream()
                .map(position -> {
                    Long memberCount = orgParticipantMemberRepository.countByOrgIdAndPositionId(orgId, position.getPositionId());
                    return PositionResDTO.builder()
                            .positionId(position.getPositionId())
                            .name(position.getName())
                            .memberCount(memberCount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public PositionResDTO updatePosition(Long positionId, PositionReqDTO positionReqDTO) {
        PositionEntity position = positionRepository.findById(positionId)
                .orElseThrow(OrgException::positionNotFoundException);

        if (!position.getName().equals(positionReqDTO.getName()) &&
                positionRepository.existsByName(positionReqDTO.getName())) {
            throw OrgException.positionNameDuplicateException();
        }

        return PositionResDTO.builder()
                .positionId(position.getPositionId())
                .name(position.getName())
                .memberCount(0L)
                .build();
    }

    @Transactional
    public void deletePosition(Long positionId) {
        PositionEntity position = positionRepository.findById(positionId)
                .orElseThrow(OrgException::positionNotFoundException);

        if (!position.getOrgParticipantMemberEntityList().isEmpty()) {
            throw OrgException.positionHasMembersException();
        }

        positionRepository.delete(position);
    }
}