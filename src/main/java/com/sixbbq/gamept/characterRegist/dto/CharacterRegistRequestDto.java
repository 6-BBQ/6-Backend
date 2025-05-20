package com.sixbbq.gamept.characterRegist.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CharacterRegistRequestDto {
    private String serverId;     // 서버 ID
    private String adventureName; // 모험단명
    private String characterName; // 캐릭터명
}
