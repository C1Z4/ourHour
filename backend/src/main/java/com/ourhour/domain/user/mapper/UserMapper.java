package com.ourhour.domain.user.mapper;

import com.ourhour.domain.auth.dto.OAuthSigninResDTO;
import com.ourhour.domain.auth.dto.SignupReqDTO;
import com.ourhour.domain.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // DTO -> Entity 변환
    @Mapping(target = "password", source = "hashedPassword")
    @Mapping(target = "platform", expression = "java(com.ourhour.domain.user.enums.Platform.OURHOUR)")
    @Mapping(target = "isEmailVerified", constant = "true")
    @Mapping(target = "emailVerifiedAt", source = "emailVerifiedAt")
    UserEntity toUserEntity(SignupReqDTO signupReqDTO, String hashedPassword, LocalDateTime emailVerifiedAt);

    // Entity -> DTO
    OAuthSigninResDTO toOAuthSigninResDTO(UserEntity userEntity);
}
