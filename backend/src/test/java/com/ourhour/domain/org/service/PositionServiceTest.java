package com.ourhour.domain.org.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ourhour.domain.org.dto.PositionReqDTO;
import com.ourhour.domain.org.dto.PositionResDTO;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.entity.PositionEntity;
import com.ourhour.domain.org.exception.OrgException;
import com.ourhour.domain.org.repository.OrgRepository;
import com.ourhour.domain.org.repository.PositionRepository;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("PositionService 테스트")
class PositionServiceTest {

    @InjectMocks
    private PositionService positionService;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private OrgRepository orgRepository;

    @Mock
    private OrgParticipantMemberRepository orgParticipantMemberRepository;

    @Test
    @DisplayName("직책 생성 성공")
    void createPosition_Success() {
        // given
        Long orgId = 1L;
        PositionReqDTO reqDTO = new PositionReqDTO();
        reqDTO.setName("팀장");

        OrgEntity orgEntity = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        PositionEntity positionEntity = PositionEntity.builder()
                .name("팀장")
                .orgEntity(orgEntity)
                .build();

        PositionEntity savedPosition = PositionEntity.builder()
                .name("팀장")
                .orgEntity(orgEntity)
                .build();

        given(orgRepository.findById(orgId)).willReturn(Optional.of(orgEntity));
        given(positionRepository.existsByNameAndOrgEntity(reqDTO.getName(), orgEntity)).willReturn(false);
        given(positionRepository.save(any(PositionEntity.class))).willReturn(savedPosition);

        // when
        PositionResDTO result = positionService.createPosition(orgId, reqDTO);

        // then
        assertThat(result.getName()).isEqualTo("팀장");
        assertThat(result.getMemberCount()).isEqualTo(0L);
        then(orgRepository).should().findById(orgId);
        then(positionRepository).should().existsByNameAndOrgEntity(reqDTO.getName(), orgEntity);
        then(positionRepository).should().save(any(PositionEntity.class));
    }

    @Test
    @DisplayName("직책 생성 실패 - 조직을 찾을 수 없음")
    void createPosition_OrgNotFound() {
        // given
        Long orgId = 1L;
        PositionReqDTO reqDTO = new PositionReqDTO();
        reqDTO.setName("팀장");

        given(orgRepository.findById(orgId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> positionService.createPosition(orgId, reqDTO))
                .isInstanceOf(OrgException.class);
    }

    @Test
    @DisplayName("직책 생성 실패 - 직책명 중복")
    void createPosition_DuplicateName() {
        // given
        Long orgId = 1L;
        PositionReqDTO reqDTO = new PositionReqDTO();
        reqDTO.setName("팀장");

        OrgEntity orgEntity = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        given(orgRepository.findById(orgId)).willReturn(Optional.of(orgEntity));
        given(positionRepository.existsByNameAndOrgEntity(reqDTO.getName(), orgEntity)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> positionService.createPosition(orgId, reqDTO))
                .isInstanceOf(OrgException.class);
    }

    @Test
    @DisplayName("모든 직책 조회")
    void getAllPositions() {
        // given
        PositionEntity position1 = PositionEntity.builder()
                .name("팀장")
                .build();

        PositionEntity position2 = PositionEntity.builder()
                .name("선임")
                .build();

        List<PositionEntity> positions = Arrays.asList(position1, position2);

        given(positionRepository.findAll()).willReturn(positions);

        // when
        List<PositionResDTO> result = positionService.getAllPositions();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("팀장");
        assertThat(result.get(1).getName()).isEqualTo("선임");
    }

    @Test
    @DisplayName("조직별 직책 조회")
    void getPositionsByOrg() {
        // given
        Long orgId = 1L;

        OrgEntity orgEntity = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        PositionEntity position1 = PositionEntity.builder()
                .name("팀장")
                .orgEntity(orgEntity)
                .build();

        List<PositionEntity> positions = Arrays.asList(position1);

        given(orgRepository.findById(orgId)).willReturn(Optional.of(orgEntity));
        given(positionRepository.findByOrgEntity(orgEntity)).willReturn(positions);
        given(orgParticipantMemberRepository.countByOrgIdAndPositionId(orgId, null)).willReturn(3L);

        // when
        List<PositionResDTO> result = positionService.getPositionsByOrg(orgId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("팀장");
        assertThat(result.get(0).getMemberCount()).isEqualTo(3L);
    }

    @Test
    @DisplayName("직책 수정 성공")
    void updatePosition_Success() {
        // given
        Long positionId = 1L;
        PositionReqDTO reqDTO = new PositionReqDTO();
        reqDTO.setName("수정된 직책명");

        PositionEntity position = PositionEntity.builder()
                .name("기존 직책명")
                .build();

        given(positionRepository.findById(positionId)).willReturn(Optional.of(position));
        given(positionRepository.existsByName(reqDTO.getName())).willReturn(false);

        // when
        PositionResDTO result = positionService.updatePosition(positionId, reqDTO);

        // then
        assertThat(result.getName()).isEqualTo("기존 직책명");
        assertThat(result.getMemberCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("직책 수정 실패 - 직책을 찾을 수 없음")
    void updatePosition_PositionNotFound() {
        // given
        Long positionId = 1L;
        PositionReqDTO reqDTO = new PositionReqDTO();
        reqDTO.setName("수정된 직책명");

        given(positionRepository.findById(positionId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> positionService.updatePosition(positionId, reqDTO))
                .isInstanceOf(OrgException.class);
    }

    @Test
    @DisplayName("직책 수정 실패 - 직책명 중복")
    void updatePosition_DuplicateName() {
        // given
        Long positionId = 1L;
        PositionReqDTO reqDTO = new PositionReqDTO();
        reqDTO.setName("중복된 직책명");

        PositionEntity position = PositionEntity.builder()
                .name("기존 직책명")
                .build();

        given(positionRepository.findById(positionId)).willReturn(Optional.of(position));
        given(positionRepository.existsByName(reqDTO.getName())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> positionService.updatePosition(positionId, reqDTO))
                .isInstanceOf(OrgException.class);
    }

    @Test
    @DisplayName("직책 삭제 성공")
    void deletePosition_Success() {
        // given
        Long positionId = 1L;

        PositionEntity position = PositionEntity.builder()
                .name("삭제할 직책")
                .orgParticipantMemberEntityList(new ArrayList<>())
                .build();

        given(positionRepository.findById(positionId)).willReturn(Optional.of(position));

        // when
        positionService.deletePosition(positionId);

        // then
        then(positionRepository).should().findById(positionId);
        then(positionRepository).should().delete(position);
    }

    @Test
    @DisplayName("직책 삭제 실패 - 직책을 찾을 수 없음")
    void deletePosition_PositionNotFound() {
        // given
        Long positionId = 1L;

        given(positionRepository.findById(positionId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> positionService.deletePosition(positionId))
                .isInstanceOf(OrgException.class);
    }

}