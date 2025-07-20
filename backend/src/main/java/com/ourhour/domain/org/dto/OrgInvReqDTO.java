package com.ourhour.domain.org.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrgInvReqDTO {

    private List<InviteInfoDTO> inviteInfoDTOList;

}
