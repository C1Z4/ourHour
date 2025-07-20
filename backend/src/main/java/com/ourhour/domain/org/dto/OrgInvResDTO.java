package com.ourhour.domain.org.dto;

import com.ourhour.domain.org.enums.InvStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrgInvResDTO {

    private List<InviteInfoDTO> inviteInfoDTOList;
    private InvStatus status;

}
