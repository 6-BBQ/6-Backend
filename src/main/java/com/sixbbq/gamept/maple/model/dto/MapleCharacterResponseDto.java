package com.sixbbq.gamept.maple.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MapleCharacterResponseDto {
    private String characterName;
    private String worldName;
    private String characterClass;
    private String ocid;
    private String representativeName;
}
