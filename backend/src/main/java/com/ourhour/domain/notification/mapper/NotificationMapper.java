package com.ourhour.domain.notification.mapper;

import com.ourhour.domain.notification.dto.NotificationCreateReqDTO;
import com.ourhour.domain.notification.dto.NotificationDTO;
import com.ourhour.domain.notification.entity.NotificationEntity;
import com.ourhour.domain.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "isRead", source = "isRead")
    NotificationDTO toDTO(NotificationEntity entity);

    List<NotificationDTO> toDTOList(List<NotificationEntity> entities);

    @Mapping(target = "userEntity", source = "user")
    @Mapping(target = "type", source = "dto.type")
    @Mapping(target = "title", source = "dto.title")
    @Mapping(target = "message", source = "dto.message")
    @Mapping(target = "relatedId", source = "dto.relatedId")
    @Mapping(target = "relatedType", source = "dto.relatedType")
    @Mapping(target = "actionUrl", source = "dto.actionUrl")
    @Mapping(target = "relatedProjectName", source = "dto.relatedProjectName")
    @Mapping(target = "isRead", constant = "false")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    NotificationEntity toEntity(NotificationCreateReqDTO dto, UserEntity user);
}