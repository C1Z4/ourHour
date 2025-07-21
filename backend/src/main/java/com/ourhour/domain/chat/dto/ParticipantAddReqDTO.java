package com.ourhour.domain.chat.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ParticipantAddReqDTO {

    private List<Long> memberIds;
}
