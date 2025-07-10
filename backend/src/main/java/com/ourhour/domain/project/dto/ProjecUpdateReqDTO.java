package com.ourhour.domain.project.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString; 
import lombok.experimental.SuperBuilder;

@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class ProjecUpdateReqDTO extends ProjectBaseDTO {
    
    private List<Long> participantIds;
}
