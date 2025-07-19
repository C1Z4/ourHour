package com.ourhour.domain.org.service;

import com.ourhour.domain.auth.service.AbstractVerificationService;
import com.ourhour.domain.auth.service.EmailSenderService;
import com.ourhour.domain.member.entity.MemberEntity;
import com.ourhour.domain.member.exceptions.MemberException;
import com.ourhour.domain.member.repository.MemberRepository;
import com.ourhour.domain.org.dto.InviteInfoDTO;
import com.ourhour.domain.org.dto.OrgInvReqDTO;
import com.ourhour.domain.org.entity.OrgInvBatchEntity;
import com.ourhour.domain.org.entity.OrgInvEntity;
import com.ourhour.domain.org.entity.OrgParticipantMemberEntity;
import com.ourhour.domain.org.enums.InvStatus;
import com.ourhour.domain.org.repository.OrgInvBatchRepository;
import com.ourhour.domain.org.repository.OrgInvRepository;
import com.ourhour.domain.org.repository.OrgParticipantMemberRepository;
import com.ourhour.global.jwt.util.UserContextHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrgInvService extends AbstractVerificationService<OrgInvEntity> {

    private final OrgInvRepository orgInvRepository;
    private final OrgInvBatchRepository orgInvBatchRepository;
    private final OrgParticipantMemberRepository orgParticipantMemberRepository;
    private final MemberRepository memberRepository;

    @Value("${spring.service.base-url-email}")
    private String serviceBaseUrl;

    public OrgInvService(EmailSenderService emailSenderService, OrgInvRepository orgInvRepository, OrgInvBatchRepository orgInvBatchRepository, OrgParticipantMemberRepository orgParticipantMemberRepository, MemberRepository memberRepository) {
        super(emailSenderService);
        this.orgInvRepository = orgInvRepository;
        this.orgParticipantMemberRepository = orgParticipantMemberRepository;
        this.memberRepository = memberRepository;
        this.orgInvBatchRepository = orgInvBatchRepository;
    }

    // 초대 링크 이메일 발송
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
                    "/api/organizations/" + orgId +"/invitation?token=",
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

    @Override
    protected OrgInvEntity buildVerificationEntity(String token, String email, LocalDateTime createdAt, LocalDateTime expiredAt, boolean isUsed) {
        return OrgInvEntity.builder()
                .token(token)
                .email(email)
                .createdAt(createdAt)
                .expiredAt(expiredAt)
                .isUsed(isUsed)
                .status(InvStatus.PENDING)
                .build();
    }

}
