package com.sixbbq.gamept.dnf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class DFCharacterResponseDTO {

    private String characterId;
    private String characterName;
    private String serverId;
    private String adventureName;
    private LocalDateTime lastUpdated;
}
