package com.ourhour.domain.user.service;

import com.ourhour.domain.auth.repository.EmailVerificationRepository;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.org.enums.Status;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;
import com.ourhour.domain.org.service.OrgRoleGuardService;
import com.ourhour.domain.user.dto.PwdChangeReqDTO;
import com.ourhour.domain.user.dto.PwdVerifyReqDTO;
import com.ourhour.domain.user.entity.UserEntity;
import com.ourhour.domain.user.util.PasswordChanger;
import com.ourhour.domain.user.util.PasswordVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.ourhour.domain.user.exception.UserException.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordVerifier passwordVerifier;
    private final PasswordChanger passwordChanger;
    private final AnonymizeUserService anonymizeUserService;
    private final EmailVerificationRepository emailVerificationRepository;
    private final MemberRepository memberRepository;
    private final OrgRoleGuardService orgRoleGuardService;
    private final OrgParticipantMemberRepository orgParticipantMemberRepository;

    // 비밀번호 변경
    @Transactional
    public void changePwd(PwdChangeReqDTO pwdChangeReqDTO) {

        String currentPwd = pwdChangeReqDTO.getCurrentPassword();
        String newPwd = pwdChangeReqDTO.getNewPassword();
        String newPwdCheck = pwdChangeReqDTO.getNewPasswordCheck();

        UserEntity userEntity = passwordVerifier.verifyPassword(currentPwd);

        // 예외 발생: 기존 비밀번호와 새 비밀번호 일치
        if (newPwd.equals(currentPwd)) {
            throw samePwd();
        }

        passwordChanger.changePassword(userEntity, newPwd, newPwdCheck);

    }

    // 비밀번호 확인
    @Transactional
    public void verifyPwd(PwdVerifyReqDTO pwdVerifyReqDTO) {

        String pwd = pwdVerifyReqDTO.getPassword();

        passwordVerifier.verifyPassword(pwd);

    }

    // 계정 탈퇴
    @Transactional
    public void deleteUser(PwdVerifyReqDTO pwdVerifyReqDTO) {

        String pwd = pwdVerifyReqDTO.getPassword();
        UserEntity userEntity = passwordVerifier.verifyPassword(pwd);

        Long userId = userEntity.getUserId();
        // UserEntity와 연결된 모든 MemberEntity 조회
        List<MemberEntity> memberEntityList = memberRepository.findByUserEntity_UserId(userId);

        // 루트 관리자 정책 확인
        orgRoleGuardService.assertNotLastRootAdminAcrossAll(memberEntityList);

        // soft delete 처리
        userEntity.markAsDeleted();

        // 탈퇴한 사용자가 속한 모든 회사의 활성상태 INACTIVE 처리
        if (!memberEntityList.isEmpty()) {
            LocalDate now = LocalDate.now();

            orgParticipantMemberRepository
                    .updateDeactivateAllMembers(memberEntityList, Status.INACTIVE, now, Status.ACTIVE);
        }

        // 탈퇴한 사용자 익명 처리
        anonymizeUserService.anonymizeUser(memberEntityList);

        // 탈퇴한 사용자의 인증 이메일 무효화
        emailVerificationRepository.invalidateByEmail(userEntity.getEmail());

    }
}
