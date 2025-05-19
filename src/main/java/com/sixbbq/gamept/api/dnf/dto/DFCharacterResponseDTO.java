package com.sixbbq.gamept.api.dnf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DFCharacterResponseDTO {

    private String characterId;
    private String characterName;
    private String serverId;
    private String adventureName;
    private LocalDateTime lastUpdated;
}
