package com.ourhour.domain.auth.service;

import com.ourhour.domain.auth.dto.SignupReqDTO;
import com.ourhour.domain.auth.dto.SignupResDTO;
import com.ourhour.domain.auth.exception.AuthException;
import com.ourhour.domain.auth.mapper.SignupMapper;
import com.ourhour.domain.user.entity.UserEntity;
import com.ourhour.domain.user.enums.Platform;
import com.ourhour.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final UserRepository userRepository;
    private final SignupMapper signupMapper;
    private final PasswordEncoder passwordEncode;
    private final EmailVerificationService emailVerificationService;

//    @Transactional
//    public SignupResDTO signup(SignupReqDTO signupReqDTO) {
//        // 이메일 중복 확인
//        if (userRepository.existsByEmail(signupReqDTO.getEmail())) {
//            throw AuthException.duplicateRequestException();
//        }
//
//        // SignupReqDTO에서 UserEntity로 변환
//        UserEntity userReqEntity = signupMapper.toUserEntity(signupReqDTO);
//
//        // 비밀번호 암호화
//       userReqEntity.setPassword(passwordEncode.encode(signupReqDTO.getPassword()));
//
//       // 자체 로그인 시 Platform OURHOUR로 세팅
//        userReqEntity.setPlatform(Platform.OURHOUR);
//
//        // 이메일 인증
//        // 인증 완료 시 is_email_verified = true (default: false)
//        // 인증 완료 시 현재 시간 저장
//
//        // 데이터베이스에 등록
//
//        // entity -> dto 변환
//    }


}
