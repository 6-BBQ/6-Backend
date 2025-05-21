package com.sixbbq.gamept.characterRegist.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CharacterRegistResponseDto {
    private boolean success;
    private String message;
    private String characterId;
    private String characterName;
    private String serverId;
    private String adventureName;

    public CharacterRegistResponseDto() {
    }

    public CharacterRegistResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
