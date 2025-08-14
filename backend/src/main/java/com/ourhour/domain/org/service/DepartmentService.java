package com.ourhour.domain.org.service;

import com.ourhour.domain.org.dto.DepartmentReqDTO;
import com.ourhour.domain.org.dto.DepartmentResDTO;
import com.ourhour.domain.org.entity.DepartmentEntity;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.exception.OrgException;
import com.ourhour.domain.org.repository.DepartmentRepository;
import com.ourhour.domain.org.repository.OrgRepository;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final OrgRepository orgRepository;
    private final OrgParticipantMemberRepository orgParticipantMemberRepository;

    @Transactional
    public DepartmentResDTO createDepartment(Long orgId, DepartmentReqDTO departmentReqDTO) {
        OrgEntity orgEntity = orgRepository.findById(orgId)
                .orElseThrow(OrgException::orgNotFoundException);
                
        if (departmentRepository.existsByNameAndOrgEntity(departmentReqDTO.getName(), orgEntity)) {
            throw OrgException.departmentNameDuplicateException();
        }

        DepartmentEntity department = DepartmentEntity.builder()
                .name(departmentReqDTO.getName())
                .orgEntity(orgEntity)
                .build();

        DepartmentEntity savedDepartment = departmentRepository.save(department);

        return DepartmentResDTO.builder()
                .deptId(savedDepartment.getDeptId())
                .name(savedDepartment.getName())
                .memberCount(0L)
                .build();
    }

    public List<DepartmentResDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(dept -> DepartmentResDTO.builder()
                        .deptId(dept.getDeptId())
                        .name(dept.getName())
                        .build())
                .collect(Collectors.toList());
    }

    public List<DepartmentResDTO> getDepartmentsByOrg(Long orgId) {
        OrgEntity orgEntity = orgRepository.findById(orgId)
                .orElseThrow(OrgException::orgNotFoundException);
                
        return departmentRepository.findByOrgEntity(orgEntity).stream()
                .map(dept -> {
                    Long memberCount = orgParticipantMemberRepository.countByOrgIdAndDeptId(orgId, dept.getDeptId());
                    return DepartmentResDTO.builder()
                            .deptId(dept.getDeptId())
                            .name(dept.getName())
                            .memberCount(memberCount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public DepartmentResDTO updateDepartment(Long deptId, DepartmentReqDTO departmentReqDTO) {
        DepartmentEntity department = departmentRepository.findById(deptId)
                .orElseThrow(OrgException::departmentNotFoundException);

        if (!department.getName().equals(departmentReqDTO.getName()) &&
                departmentRepository.existsByName(departmentReqDTO.getName())) {
            throw OrgException.departmentNameDuplicateException();
        }

        return DepartmentResDTO.builder()
                .deptId(department.getDeptId())
                .name(department.getName())
                .memberCount(0L)
                .build();
    }

    @Transactional
    public void deleteDepartment(Long deptId) {
        DepartmentEntity department = departmentRepository.findById(deptId)
                .orElseThrow(OrgException::departmentNotFoundException);

        if (!department.getOrgParticipantMemberEntityList().isEmpty()) {
            throw OrgException.departmentHasMembersException();
        }

        departmentRepository.delete(department);
    }
}
