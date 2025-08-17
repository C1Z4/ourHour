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

    @Value("${spring.service.url.front}")
    private String serviceBaseUrl;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public OrgInvService(EmailSenderService emailSenderService, OrgInvRepository orgInvRepository,
            OrgInvBatchRepository orgInvBatchRepository, OrgParticipantMemberRepository orgParticipantMemberRepository,
            MemberRepository memberRepository, UserRepository userRepository, OrgRepository orgRepository,
            OrgInvMapper orgInvMapper) {
        super(emailSenderService);
        this.orgInvRepository = orgInvRepository;
        this.orgParticipantMemberRepository = orgParticipantMemberRepository;
        this.memberRepository = memberRepository;
        this.orgInvBatchRepository = orgInvBatchRepository;
        this.userRepository = userRepository;
        this.orgRepository = orgRepository;
        this.orgInvMapper = orgInvMapper;
    }

    // 초대 링크 이메일 발송
    // STATUS = PENDING, isUsed = false
    // TODO : 비동기 처리하기
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

            // 이미 조직에 참여 중인지 확인
            boolean alreadyMember = orgParticipantMemberRepository.existsByOrgEntity_OrgIdAndMemberEntity_Email(orgId, email);            if (alreadyMember) {
                throw MemberException.memberAlreadyExistsException();
            }

            String token = sendVerificationEmail(
                    email,
                    serviceBaseUrl,
                    "/org/" + orgId + "/invite/verify/?token=",
                    subject,
                    contentTemplate,
                    linkName);

            // orgInvEntity 생성
            OrgInvEntity orgInvEntity = OrgInvEntity.create(
                    orgInvBatchEntity,
                    token,
                    email,
                    orgInvReqDTO.getRole(),
                    LocalDateTime.now().plusMinutes(15));
            orgInvEntityList.add(orgInvEntity);
        }

        orgInvRepository.saveAll(orgInvEntityList);

    }

    // 회사에 참여하기 위한 이메일 인증하기
    // STATUS = PENDING, isUsed = true
    @Transactional
    public void verifyInvEmail(String token) {

        OrgInvEntity inv = orgInvRepository.findByToken(token)
                .orElseThrow(() -> AuthException.invalidEmailVerificationTokenException());

        // 만료 검사 & 상태 전이
        if (inv.getExpiredAt() != null && inv.getExpiredAt().isBefore(LocalDateTime.now())) {
            if (inv.getStatus() != InvStatus.EXPIRED) {
                inv.changeStatusToExpired();
            }
            throw AuthException.emailVerificationExpiredException();
        }

        // 이미 참여
        if (inv.getStatus() == InvStatus.ACCEPTED) {
            return;
        }

        // 최초 검증 시에만 true 세팅
        if (!inv.isUsed()) {
            inv.setUsed(true);
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
            throw AuthException.emailVerificationRequiredException();
        }

        // 이미 수락되었는지 확인
        if (orgInvEntity.getStatus() == InvStatus.ACCEPTED) {
            throw AuthException.emailAlreadyAcceptedException();
        }

        // 이미 완료되었는지 확인
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

        MemberEntity memberToJoin = memberRepository.findByUserEntity_UserIdAndEmail(userId, invitedEmail)
                .orElseGet(() -> {
                    String randomName = "User" + UUID.randomUUID().toString().substring(0,4);
                    MemberEntity newMember = MemberEntity.builder()
                            .userEntity(userEntity)
                            .name(randomName)
                            .email(invitedEmail)
                            .build();
                    memberRepository.flush();
                    return memberRepository.save(newMember);
                });

        System.out.println("멤버 아이디: " + memberToJoin.getMemberId());
        System.out.println(
                "멤버 아이디 타입: " +
                        (memberToJoin.getMemberId() != null
                                ? memberToJoin.getMemberId().getClass().getName()
                                : "null")
        );

        // 이미 조직에 참여 중인지 확인
        boolean alreadyParticipant = orgParticipantMemberRepository.existsByOrgEntityAndMemberEntity(orgEntity,
                memberToJoin);
        if (alreadyParticipant) {
            // 의미 없는 초대이므로 상태값만 바꾸고 메소드 종료
            orgInvEntity.changeStatusToAccepted();
            return;
        }

        // OrgParticipantMemberEntity 생성 및 저장 (실제 팀 참여)
        OrgParticipantMemberEntity newOpm = OrgParticipantMemberEntity.builder()
                .orgParticipantMemberId(new OrgParticipantMemberId(orgEntity.getOrgId(), memberToJoin.getMemberId()))
                .orgEntity(orgEntity)
                .memberEntity(memberToJoin)
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
