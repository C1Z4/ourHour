package com.ourhour.domain.org.service;

import com.ourhour.domain.auth.exception.AuthException;
import com.ourhour.domain.auth.service.AbstractVerificationService;
import com.ourhour.domain.auth.service.EmailSenderService;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.exceptions.MemberException;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.org.dto.InviteInfoDTO;
import com.ourhour.domain.org.dto.OrgInvReqDTO;
import com.ourhour.domain.org.dto.OrgInvResDTO;
import com.ourhour.domain.org.dto.OrgJoinReqDTO;
import com.ourhour.domain.org.entity.OrgEntity;
import com.ourhour.domain.org.entity.OrgInvBatchEntity;
import com.ourhour.domain.org.entity.OrgInvEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.org.enums.InvStatus;
import com.ourhour.domain.org.enums.Status;
import com.ourhour.domain.org.mapper.OrgInvMapper;
import com.ourhour.domain.org.repository.OrgInvBatchRepository;
import com.ourhour.domain.org.repository.OrgInvRepository;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;
import com.ourhour.domain.org.repository.OrgRepository;
import com.ourhour.domain.user.entity.UserEntity;
import com.ourhour.domain.user.repository.UserRepository;
import com.ourhour.global.jwt.util.UserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.ourhour.domain.auth.exception.AuthException.emailVerificationException;
import static com.ourhour.domain.member.exceptions.MemberOrgException.orgNotFoundException;
import static com.ourhour.domain.org.exceptions.OrgException.invitationException;

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

    @Value("${spring.service.base-url-email}")
    private String serviceBaseUrl;

    public OrgInvService(EmailSenderService emailSenderService, OrgInvRepository orgInvRepository, OrgInvBatchRepository orgInvBatchRepository, OrgParticipantMemberRepository orgParticipantMemberRepository, MemberRepository memberRepository, UserRepository userRepository, OrgRepository orgRepository, OrgInvMapper orgInvMapper) {
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
    public void sendInvLink(Long orgId,OrgInvReqDTO orgInvReqDTO) {

        // 해당 userId가 orgId의 어떤 memberEntity인지 확인
        Long userId = UserContextHolder.get().getUserId();
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
        String subject = "[OURHOUR] 이메일 인증 안내";
        String contentTemplate = "<p>팀 합류를 위해 아래 링크를 클릭해주세요!\n</p>";
        String linkName = "팀 참여하기";

        // 일괄 저장을 위한 리스트
        List<OrgInvEntity> orgInvEntityList = new ArrayList<>();
        List<InviteInfoDTO> inviteInfoDTOList = orgInvReqDTO
                .getInviteInfoDTOList();

        for (InviteInfoDTO inviteInfoDTO:inviteInfoDTOList) {
            String email = inviteInfoDTO.getEmail();
            String token = sendVerificationEmail(
                    email,
                    serviceBaseUrl,
                    "/api/organizations/invitation/verify?token=",
                    subject,
                    contentTemplate,
                    linkName);

            // orgInvEntity 생성
            OrgInvEntity orgInvEntity = OrgInvEntity.create(
                    orgInvBatchEntity,
                    token,
                    email,
                    inviteInfoDTO.getRole(),
                    LocalDateTime.now().plusMinutes(15)
            );
            orgInvEntityList.add(orgInvEntity);
        }

        orgInvRepository.saveAll(orgInvEntityList);

    }

    // 회사에 참여하기 위한 이메일 인증하기
    // STATUS = PENDING, isUsed = true
    @Transactional
    public void verifyInvEmail(String token) {

        OrgInvEntity inv = orgInvRepository.findByToken(token)
                .orElseThrow(() -> emailVerificationException("유효하지 않은 초대링크입니다."));

        // 만료 검사 & 상태 전이
        if (inv.getExpiredAt() != null && inv.getExpiredAt().isBefore(LocalDateTime.now())) {
            if (inv.getStatus() != InvStatus.EXPIRED) {
                inv.changeStatusToExpired();
            }
            throw emailVerificationException("초대 링크가 만료되었습니다.");
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
                .orElseThrow(() -> emailVerificationException("유효하지 않은 초대링크입니다."));

        // 이미 인증되었는지 확인
        boolean isVerified = orgInvEntity.isUsed();
        if(!isVerified) {
            throw emailVerificationException("아직 인증되지 않았습니다. 이메일 인증을 먼저 해주세요.");
        }

        // 이미 수락되었는지 확인
        if (orgInvEntity.getStatus() == InvStatus.ACCEPTED) {
            throw invitationException("이미 참여가 완료된 이메일 링크입니다.");
        }

        // 이미 완료되었는지 확인
        if (orgInvEntity.getExpiredAt().isBefore(LocalDateTime.now())) {
            if (orgInvEntity.getStatus() != InvStatus.EXPIRED) {
                orgInvEntity.changeStatusToExpired();
            }
            throw invitationException("만료된 초대 링크입니다.");
        }

        // 초대된 이메일
        String invitedEmail = orgInvEntity.getEmail();

        // 현재 로그인 유저 확인
        Long userId = UserContextHolder.get().getUserId();
        UserEntity userEntity = userRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(AuthException::userNotFoundException);

        // 로그인 시 유저 이메일 확인
        String userEmail = userEntity.getEmail();

        // 현재 로그인 유저 이메일과 초대 이메일 일치 여부
        boolean isEqualInvEmailAndUserEmail = invitedEmail.equals(userEmail);

        // 현재 로그인 유저 이메일과 초대 이메일 불일치 멤버 이메일 일치 여부 확인
        if (!isEqualInvEmailAndUserEmail) {

            // 로그인 유저가 가지고 있는 멤버 정보 확인
            List<MemberEntity> memberEntityList = memberRepository.findByUserEntity_UserId(userEntity.getUserId());
            // 멤버 정보의 이메일과 초대 이메일 일치 여부
            boolean isEqualInvEmailAndMemEmail = memberEntityList.stream().anyMatch(
                    member -> member.getEmail().equals(invitedEmail)
            );

            if (!isEqualInvEmailAndMemEmail) {
                throw invitationException("로그인된 계정의 이메일과 초대받은 이메일이 일치하지 않습니다."
                        + "현재 계정에 [" + invitedEmail + "] 이메일을 멤버에 등록 후 다시 시도하거나, "
                        + "[" + invitedEmail + "] 이메일로 로그인해주세요."
                        );
            }

        }

        /* ***************************************************************
         * 이 지점에 도달하면, 초대 이메일이 현재 로그인된 계정의 메인 이메일이거나,
         * 현재 계정과 연결된 MemberEntity 중 하나의 이메일임이 확인된 상태
         * ***************************************************************/

        // 현재 등록하려는 회사 조회
        Long batchId = orgInvEntity.getOrgInvBatchEntity().getBatchId();
        Long orgId = orgInvBatchRepository.findOrgIdByBatchId(batchId);
        OrgEntity orgEntity = orgRepository.findByOrgId(orgId);
        if (orgEntity == null) {
            throw orgNotFoundException();
        }

        MemberEntity memberToJoin = null;
        // 현재 로그인 유저 이메일과 초대 이메일이 일치하는 경우
        if (isEqualInvEmailAndUserEmail) {

            memberToJoin = memberRepository.findByUserEntity_UserIdAndEmail(userId, invitedEmail)
                    .orElseGet(() -> {
                       MemberEntity newMember = MemberEntity.builder()
                               .userEntity(userEntity)
                               .name(" ")
                               .email(invitedEmail)
                               .build();
                       return memberRepository.save(newMember);
                    });
        } else {
            memberToJoin = memberRepository.findByUserEntity_UserIdAndEmail(userId, invitedEmail)
                    .orElseThrow(() -> new IllegalStateException("위의 로직을 거쳤다면 초대 이메일과 일치하는 멤버가 없으면 안됨."));

        }

        // 이미 조직에 참여 중인지 확인
        boolean alreadyParticipant = orgParticipantMemberRepository.existsByOrgEntityAndMemberEntity(orgEntity, memberToJoin);
        if (alreadyParticipant) {
            // 의미 없는 초대이므로 상태값만 바꾸고 메소드 종료
            orgInvEntity.changeStatusToAccepted();
            return;
        }

        // OrgParticipantMemberEntity 생성 및 저장 (실제 팀 참여)
        OrgParticipantMemberEntity newOpm = OrgParticipantMemberEntity.builder()
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

        // DTO 변환
        List<OrgInvResDTO> orgInvResDTO = orgInvEntityList.stream()
                .map(orgInvMapper::toOrgInvResDTO)
                .toList();

        return orgInvResDTO;

    }

    // 만료시간 스케쥴링
    @Scheduled(cron = "0 1 0 * * ?") // 매일 0시 1분마다 실행
    @Transactional
    public void expireOldInvitations() {

        log.info("만료된 초대 링크 처리 시작. 헌재 시간 : {}", LocalDateTime.now());

        // PENDING 상태이고, expiredAt이 현재 시간보다 이전인 모든 초대 조회
        List<OrgInvEntity> expiredInvitationList = orgInvRepository
                .findByStatusAndExpiredAtBefore(InvStatus.PENDING, LocalDateTime.now());

        if(expiredInvitationList.isEmpty()) {
            log.info("만료된 초대 링크가 없습니다.");
            return;
        }

        for (OrgInvEntity orgInvEntity:expiredInvitationList) {
            orgInvEntity.changeStatusToExpired();
            log.debug("초대링크 만료처리 : token={}, email={}", orgInvEntity.getToken(), orgInvEntity.getEmail());
        }

        log.info("{}개의 초대링크 만료처리 완료", expiredInvitationList.size());
    }


    @Override
    protected OrgInvEntity buildVerificationEntity(String token, String email, LocalDateTime createdAt, LocalDateTime expiredAt, boolean isUsed) {
        return OrgInvEntity.builder()
                .token(token)
                .email(email)
                .createdAt(createdAt)
                .expiredAt(expiredAt)
                .isUsed(isUsed)
                .build();
    }

}
