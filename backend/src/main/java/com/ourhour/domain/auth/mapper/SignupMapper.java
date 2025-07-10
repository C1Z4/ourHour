package com.ourhour.domain.auth.mapper;

import com.ourhour.domain.auth.dto.SignupReqDTO;
import com.ourhour.domain.auth.dto.SignupResDTO;
import com.ourhour.domain.user.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SignupMapper {

    // SignupReqDTO -> UserEntity로 변환
    UserEntity toUserEntity(SignupReqDTO dto);

    // UserEntity -> SignupResDTO 변환
    SignupResDTO toSignupResDTO(UserEntity entity);
}
