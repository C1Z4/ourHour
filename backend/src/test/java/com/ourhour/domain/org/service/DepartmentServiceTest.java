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

import com.ourhour.domain.org.dto.DepartmentReqDTO;
import com.ourhour.domain.org.dto.DepartmentResDTO;
import com.ourhour.domain.org.entity.DepartmentEntity;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.exception.OrgException;
import com.ourhour.domain.org.repository.DepartmentRepository;
import com.ourhour.domain.org.repository.OrgRepository;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("DepartmentService 테스트")
class DepartmentServiceTest {

    @InjectMocks
    private DepartmentService departmentService;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private OrgRepository orgRepository;

    @Mock
    private OrgParticipantMemberRepository orgParticipantMemberRepository;

    @Test
    @DisplayName("부서 생성 성공")
    void createDepartment_Success() {
        // given
        Long orgId = 1L;
        DepartmentReqDTO reqDTO = new DepartmentReqDTO();
        reqDTO.setName("개발팀");

        OrgEntity orgEntity = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        DepartmentEntity departmentEntity = DepartmentEntity.builder()
                .name("개발팀")
                .orgEntity(orgEntity)
                .build();

        DepartmentEntity savedDepartment = DepartmentEntity.builder()
                .name("개발팀")
                .orgEntity(orgEntity)
                .build();

        given(orgRepository.findById(orgId)).willReturn(Optional.of(orgEntity));
        given(departmentRepository.existsByNameAndOrgEntity(reqDTO.getName(), orgEntity)).willReturn(false);
        given(departmentRepository.save(any(DepartmentEntity.class))).willReturn(savedDepartment);

        // when
        DepartmentResDTO result = departmentService.createDepartment(orgId, reqDTO);

        // then
        assertThat(result.getName()).isEqualTo("개발팀");
        assertThat(result.getMemberCount()).isEqualTo(0L);
        then(orgRepository).should().findById(orgId);
        then(departmentRepository).should().existsByNameAndOrgEntity(reqDTO.getName(), orgEntity);
        then(departmentRepository).should().save(any(DepartmentEntity.class));
    }

    @Test
    @DisplayName("부서 생성 실패 - 조직을 찾을 수 없음")
    void createDepartment_OrgNotFound() {
        // given
        Long orgId = 1L;
        DepartmentReqDTO reqDTO = new DepartmentReqDTO();
        reqDTO.setName("개발팀");

        given(orgRepository.findById(orgId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> departmentService.createDepartment(orgId, reqDTO))
                .isInstanceOf(OrgException.class);
    }

    @Test
    @DisplayName("부서 생성 실패 - 부서명 중복")
    void createDepartment_DuplicateName() {
        // given
        Long orgId = 1L;
        DepartmentReqDTO reqDTO = new DepartmentReqDTO();
        reqDTO.setName("개발팀");

        OrgEntity orgEntity = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        given(orgRepository.findById(orgId)).willReturn(Optional.of(orgEntity));
        given(departmentRepository.existsByNameAndOrgEntity(reqDTO.getName(), orgEntity)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> departmentService.createDepartment(orgId, reqDTO))
                .isInstanceOf(OrgException.class);
    }

    @Test
    @DisplayName("모든 부서 조회")
    void getAllDepartments() {
        // given
        DepartmentEntity dept1 = DepartmentEntity.builder()
                .name("개발팀")
                .build();

        DepartmentEntity dept2 = DepartmentEntity.builder()
                .name("디자인팀")
                .build();

        List<DepartmentEntity> departments = Arrays.asList(dept1, dept2);

        given(departmentRepository.findAll()).willReturn(departments);

        // when
        List<DepartmentResDTO> result = departmentService.getAllDepartments();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("개발팀");
        assertThat(result.get(1).getName()).isEqualTo("디자인팀");
    }

    @Test
    @DisplayName("조직별 부서 조회")
    void getDepartmentsByOrg() {
        // given
        Long orgId = 1L;

        OrgEntity orgEntity = OrgEntity.builder()
                .name("테스트 조직")
                .email("test@org.com")
                .phone("010-1234-5678")
                .build();

        DepartmentEntity dept1 = DepartmentEntity.builder()
                .name("개발팀")
                .orgEntity(orgEntity)
                .build();

        List<DepartmentEntity> departments = Arrays.asList(dept1);

        given(orgRepository.findById(orgId)).willReturn(Optional.of(orgEntity));
        given(departmentRepository.findByOrgEntity(orgEntity)).willReturn(departments);
        given(orgParticipantMemberRepository.countByOrgIdAndDeptId(orgId, null)).willReturn(5L);

        // when
        List<DepartmentResDTO> result = departmentService.getDepartmentsByOrg(orgId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("개발팀");
        assertThat(result.get(0).getMemberCount()).isEqualTo(5L);
    }

    @Test
    @DisplayName("부서 수정 성공")
    void updateDepartment_Success() {
        // given
        Long deptId = 1L;
        DepartmentReqDTO reqDTO = new DepartmentReqDTO();
        reqDTO.setName("수정된 부서명");

        DepartmentEntity department = DepartmentEntity.builder()
                .name("기존 부서명")
                .build();

        given(departmentRepository.findById(deptId)).willReturn(Optional.of(department));
        given(departmentRepository.existsByName(reqDTO.getName())).willReturn(false);

        // when
        DepartmentResDTO result = departmentService.updateDepartment(deptId, reqDTO);

        // then
        assertThat(result.getName()).isEqualTo("기존 부서명");
        assertThat(result.getMemberCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("부서 수정 실패 - 부서를 찾을 수 없음")
    void updateDepartment_DepartmentNotFound() {
        // given
        Long deptId = 1L;
        DepartmentReqDTO reqDTO = new DepartmentReqDTO();
        reqDTO.setName("수정된 부서명");

        given(departmentRepository.findById(deptId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> departmentService.updateDepartment(deptId, reqDTO))
                .isInstanceOf(OrgException.class);
    }

    @Test
    @DisplayName("부서 수정 실패 - 부서명 중복")
    void updateDepartment_DuplicateName() {
        // given
        Long deptId = 1L;
        DepartmentReqDTO reqDTO = new DepartmentReqDTO();
        reqDTO.setName("중복된 부서명");

        DepartmentEntity department = DepartmentEntity.builder()
                .name("기존 부서명")
                .build();

        given(departmentRepository.findById(deptId)).willReturn(Optional.of(department));
        given(departmentRepository.existsByName(reqDTO.getName())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> departmentService.updateDepartment(deptId, reqDTO))
                .isInstanceOf(OrgException.class);
    }

    @Test
    @DisplayName("부서 삭제 성공")
    void deleteDepartment_Success() {
        // given
        Long deptId = 1L;

        DepartmentEntity department = DepartmentEntity.builder()
                .name("삭제할 부서")
                .orgParticipantMemberEntityList(new ArrayList<>())
                .build();

        given(departmentRepository.findById(deptId)).willReturn(Optional.of(department));

        // when
        departmentService.deleteDepartment(deptId);

        // then
        then(departmentRepository).should().findById(deptId);
        then(departmentRepository).should().delete(department);
    }

    @Test
    @DisplayName("부서 삭제 실패 - 부서를 찾을 수 없음")
    void deleteDepartment_DepartmentNotFound() {
        // given
        Long deptId = 1L;

        given(departmentRepository.findById(deptId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> departmentService.deleteDepartment(deptId))
                .isInstanceOf(OrgException.class);
    }

}