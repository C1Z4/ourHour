package com.ourhour.domain.org.service;

import com.ourhour.domain.auth.exception.AuthException;
import com.ourhour.domain.auth.service.AbstractVerificationService;
import com.ourhour.domain.auth.service.EmailSenderService;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.exception.MemberException;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.org.dto.OrgInvReqDTO;
import com.ourhour.domain.org.dto.OrgInvResDTO;
import com.ourhour.domain.org.dto.OrgJoinReqDTO;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.entity.OrgInvBatchEntity;
import com.ourhour.domain.org.entity.OrgInvEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberId;
import com.ourhour.domain.org.enums.InvStatus;
import com.ourhour.domain.org.enums.Role;
import com.ourhour.domain.org.enums.Status;
import com.ourhour.domain.org.exception.OrgException;
import com.ourhour.domain.org.exception.OrgInvExcception;
import com.ourhour.domain.org.mapper.OrgInvMapper;
import com.ourhour.domain.org.repository.OrgInvBatchRepository;
import com.ourhour.domain.org.repository.OrgInvRepository;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;
import com.ourhour.domain.org.repository.OrgRepository;
import com.ourhour.domain.user.entity.UserEntity;
import com.ourhour.domain.user.repository.UserRepository;
import com.ourhour.global.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Slf4j
public class OrgInvService extends AbstractVerificationService<OrgInvEntity> {

    private final OrgInvRepository orgInvRepository;
    private final OrgInvBatchRepository orgInvBatchRepository;
    private final OrgParticipantMemberRepository orgParticipantMemberRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final OrgRepository orgRepository;
    private final OrgInvMapper orgInvMapper;
    private final OrgRoleGuardService orgRoleGuardService;

    @Value("${spring.service.url.front}")
    private String serviceBaseUrl;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public OrgInvService(EmailSenderService emailSenderService, OrgInvRepository orgInvRepository,
            OrgInvBatchRepository orgInvBatchRepository, OrgParticipantMemberRepository orgParticipantMemberRepository,
            MemberRepository memberRepository, UserRepository userRepository, OrgRepository orgRepository,
            OrgInvMapper orgInvMapper, OrgRoleGuardService orgRoleGuardService) {
        super(emailSenderService);
        this.orgInvRepository = orgInvRepository;
        this.orgParticipantMemberRepository = orgParticipantMemberRepository;
        this.memberRepository = memberRepository;
        this.orgInvBatchRepository = orgInvBatchRepository;
        this.userRepository = userRepository;
        this.orgRepository = orgRepository;
        this.orgInvMapper = orgInvMapper;
        this.orgRoleGuardService = orgRoleGuardService;
    }

    // 초대 링크 이메일 발송
    // STATUS = PENDING, isUsed = false
    @Transactional
    public void sendInvLink(Long orgId, List<OrgInvReqDTO> orgInvReqDTOList) {

        Long userId = SecurityUtil.getCurrentUserId();

        // 해당 유저의 이메일 확인
        String currentUserEmail = userRepository.findByUserIdAndIsDeletedFalse(userId).get().getEmail();

        // 해당 userId가 orgId의 어떤 memberEntity인지 확인
        MemberEntity memberEntity = memberRepository
                .findMemberInOrgByUserId(orgId, userId).orElseThrow(MemberException::memberNotFoundException);

        OrgParticipantMemberEntity orgParticipantMemberEntity = orgParticipantMemberRepository
                .findByOrgEntity_OrgIdAndMemberEntity_MemberId(orgId, memberEntity.getMemberId());

        // 배치 생성 및 저장
        OrgInvBatchEntity orgInvBatchEntity = OrgInvBatchEntity.builder()
                .orgParticipantMemberEntity(orgParticipantMemberEntity)
                .build();
        orgInvBatchRepository.save(orgInvBatchEntity);

        // 메일 세팅
        String subject = "[OURHOUR] 팀 참여 인증 안내";
        String contentTemplate = "<p>팀 합류를 위해 아래 링크를 클릭해주세요!\n</p>";
        String linkName = "팀 참여하기";

        // 일괄 저장을 위한 리스트
        List<OrgInvEntity> orgInvEntityList = new ArrayList<>();

        // DB 저장용 엔티티 생성
        for (OrgInvReqDTO orgInvReqDTO : orgInvReqDTOList) {
            String email = orgInvReqDTO.getEmail();

            // 이메일 형식 체크
            if (!EMAIL_PATTERN.matcher(email).matches()){
                throw AuthException.invalidEmailFormatException();
            }

            // 본인 이메일 초대 금지
            if (email.equalsIgnoreCase(currentUserEmail)) {
                throw OrgInvExcception.selfInvitationNotAllowedException();
            }

            // 루트 관리자 정책 (최대 2명)
            if (orgInvReqDTO.getRole() == Role.ROOT_ADMIN) {
                int currentRootAdminCount = orgRoleGuardService.countActiveRootAdmins(orgId);

                if (currentRootAdminCount >= 2) {
                    throw OrgException.tooMuchRootAdminException();
                }
            }

            // 이미 조직에 참여 중인지 확인
            boolean alreadyMember = orgParticipantMemberRepository.existsByOrgEntity_OrgIdAndMemberEntity_Email(orgId, email);
            if (alreadyMember) {
                throw MemberException.memberAlreadyExistsException();
            }

            // 토큰 생성
            String token = UUID.randomUUID().toString();

            // orgInvEntity 생성
            OrgInvEntity orgInvEntity = OrgInvEntity.create(
                    orgInvBatchEntity,
                    token,
                    email,
                    orgInvReqDTO.getRole(),
                    LocalDateTime.now().plusMinutes(15));
            orgInvEntityList.add(orgInvEntity);
        }

        // DB 일괄 저장
        orgInvRepository.saveAll(orgInvEntityList);

        // Batch 단위 async 발송
        int batchSize = 50;

        // 전체 리스트를 배치 단위로 나누어 처리
        for (int i = 0; i < orgInvReqDTOList.size(); i+=batchSize) {

            // 마지막 배치가 리스트 크기를 넘어가지 않도록 현재 배치의 끝 인덱스 계산
            int end = Math.min(i+batchSize, orgInvReqDTOList.size());

            // 원본 리스트에서 현재 배치 범위만큼 잘라서 사용
            List<OrgInvEntity> subList = orgInvEntityList.subList(i, end);

            // 현재 배치의 각 아이템을 순회하면 처리
            subList.forEach(inv -> {
                sendEmailAsync(
                        inv.getToken(),
                        inv.getEmail(),
                        serviceBaseUrl,
                        "/org/" + orgId + "/invite/verify/?token=",
                        subject,
                        contentTemplate,
                        linkName);
            });
        }
    }

    // 회사에 참여하기 위한 이메일 인증하기
    // STATUS = PENDING, isUsed = true
    @Transactional
    public void verifyInvEmail(String token) {

        OrgInvEntity orgInvEntity = orgInvRepository.findByToken(token)
                .orElseThrow(() -> AuthException.invalidEmailVerificationTokenException());

        // 만료 검사 & 상태 전이
        if (orgInvEntity.getExpiredAt() != null && orgInvEntity.getExpiredAt().isBefore(LocalDateTime.now())) {
            if (orgInvEntity.getStatus() != InvStatus.EXPIRED) {
                orgInvEntity.changeStatusToExpired();
            }
            throw AuthException.emailVerificationExpiredException();
        }

        // 이미 참여
        if (orgInvEntity.getStatus() == InvStatus.ACCEPTED) {
            return;
        }

        // 최초 검증 시에만 true 세팅
        if (!orgInvEntity.isUsed()) {
            orgInvEntity.setUsed(true);
        }

    }

    // 회사에 참여하기
    // STATUS = ACCEPTED, isUsed = true
    @Transactional
    public void acceptInvEmail(OrgJoinReqDTO orgJoinReqDTO) {

        // 토큰 조회
        OrgInvEntity orgInvEntity = orgInvRepository.findByToken(orgJoinReqDTO.getToken())
                .orElseThrow(AuthException::invalidEmailVerificationTokenException);

        // 이미 인증되었는지 확인
        boolean isVerified = orgInvEntity.isUsed();
        if (!isVerified) {
            throw AuthException.emailAlreadyVerifiedException();
        }

        // 이미 수락되었는지 확인
        if (orgInvEntity.getStatus() == InvStatus.ACCEPTED) {
            throw AuthException.emailAlreadyAcceptedException();
        }

        // 초대 메일 만료되었는지 확인
        if (orgInvEntity.getExpiredAt().isBefore(LocalDateTime.now())) {
            if (orgInvEntity.getStatus() != InvStatus.EXPIRED) {
                orgInvEntity.changeStatusToExpired();
            }
            throw AuthException.emailVerificationExpiredException();
        }

        // 초대된 이메일
        String invitedEmail = orgInvEntity.getEmail();

        // 현재 로그인 유저 확인
        Long userId = SecurityUtil.getCurrentUserId();
        UserEntity userEntity = userRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(AuthException::userNotFoundException);

        // 로그인 시 유저 이메일 확인
        String userEmail = userEntity.getEmail();

        // 현재 로그인 유저 이메일과 초대 이메일 일치 여부
        boolean isEqualInvEmailAndUserEmail = invitedEmail.equals(userEmail);
        if (!isEqualInvEmailAndUserEmail) {
                throw AuthException.emailNotMatchException("로그인된 계정의 이메일과 초대받은 이메일이 일치하지 않습니다. [" + invitedEmail + "] 이메일로 로그인해주세요.");
        }

        // 현재 등록하려는 회사 조회
        Long batchId = orgInvEntity.getOrgInvBatchEntity().getBatchId();
        Long orgId = orgInvBatchRepository.findOrgIdByBatchId(batchId);
        OrgEntity orgEntity = orgRepository.findByOrgId(orgId);
        if (orgEntity == null) {
            throw OrgException.orgNotFoundException();
        }

        List<MemberEntity> members = memberRepository.findAllByUserEntity_UserIdAndEmail(userId, invitedEmail);

        for (MemberEntity member : members) {
            boolean alreadyParticipant = orgParticipantMemberRepository.existsByOrgEntityAndMemberEntity(orgEntity,
                    member);
            if (alreadyParticipant) {
                // 의미 없는 초대이므로 상태값만 바꾸고 메소드 종료
                orgInvEntity.changeStatusToExpired();
                return;
            }
        }

        String randomName = "User" + UUID.randomUUID().toString().substring(0,4);
        MemberEntity newMember = MemberEntity.builder()
                .userEntity(userEntity)
                .name(randomName)
                .email(invitedEmail)
                .build();
        memberRepository.save(newMember);

        // OrgParticipantMemberEntity 생성 및 저장 (실제 팀 참여)
        OrgParticipantMemberEntity newOpm = OrgParticipantMemberEntity.builder()
                .orgParticipantMemberId(new OrgParticipantMemberId(orgEntity.getOrgId(), newMember.getMemberId()))
                .orgEntity(orgEntity)
                .memberEntity(newMember)
                .role(orgInvEntity.getRole())
                .status(Status.ACTIVE)
                .joinedAt(LocalDate.now())
                .build();
        orgParticipantMemberRepository.save(newOpm);

        // 초대 상태 ACCEPTED로 변경
        orgInvEntity.changeStatusToAccepted();
    }

    // 초대 목록 및 상태 조회
    public List<OrgInvResDTO> getInvList(Long orgId) {

        // 해당 회사의 초대 조회
        List<OrgInvBatchEntity> orgInvBatchEntityList = orgInvBatchRepository.findAllByOrgId(orgId);
        List<Long> batchIds = orgInvBatchEntityList.stream()
                .map(OrgInvBatchEntity::getBatchId)
                .toList();

        List<OrgInvEntity> orgInvEntityList = orgInvRepository.findAllByBatchIds(batchIds);

        List<OrgInvResDTO> orgInvResDTOList = orgInvEntityList.stream().map(orgInvMapper::toOrgInvResDTO).toList();

        return orgInvResDTOList;

    }

    // 만료시간 스케쥴링
    @Scheduled(cron = "0 1 0 * * ?") // 매일 0시 1분마다 실행
    @Transactional
    public void expireOldInvitations() {

        log.info("만료된 초대 링크 처리 시작. 헌재 시간 : {}", LocalDateTime.now());

        // PENDING 상태이고, expiredAt이 현재 시간보다 이전인 모든 초대 조회
        List<OrgInvEntity> expiredInvitationList = orgInvRepository
                .findByStatusAndExpiredAtBefore(InvStatus.PENDING, LocalDateTime.now());

        if (expiredInvitationList.isEmpty()) {
            log.info("만료된 초대 링크가 없습니다.");
            return;
        }

        for (OrgInvEntity orgInvEntity : expiredInvitationList) {
            orgInvEntity.changeStatusToExpired();
            log.debug("초대링크 만료처리 : token={}, email={}", orgInvEntity.getToken(), orgInvEntity.getEmail());
        }

        log.info("{}개의 초대링크 만료처리 완료", expiredInvitationList.size());
    }

    @Override
    protected OrgInvEntity buildVerificationEntity(String token, String email, LocalDateTime createdAt,
            LocalDateTime expiredAt, boolean isUsed) {
        return OrgInvEntity.builder()
                .token(token)
                .email(email)
                .createdAt(createdAt)
                .expiredAt(expiredAt)
                .isUsed(isUsed)
                .build();
    }

}
