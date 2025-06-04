package com.sixbbq.gamept.characterRegist.dto;


import com.sixbbq.gamept.characterRegist.entity.CharacterRegist;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class CharacterRegistDTO {
    private String userId;  // 사용자 ID (Member 엔티티 참조)
    private String serverId;  // 서버 ID
    private String characterId;  // 캐릭터 ID
    private String imageUrl; // 캐릭터 이미지 링크
    private String characterName;  // 캐릭터 이름
    private String adventureName;  // 모험단명
    private LocalDateTime createdAt;
    private LocalDateTime aiRequestTime;
    private int aiRequestCount;

    protected CharacterRegistDTO() {}

    public CharacterRegistDTO(CharacterRegist character, String imageUrl) {
        this.userId = character.getUserId();
        this.serverId = character.getServerId();
        this.characterId = character.getCharacterId();
        this.characterName = character.getCharacterName();
        this.adventureName = character.getAdventureName();
        this.createdAt = character.getCreatedAt();
        this.aiRequestTime = character.getAiRequestTime();
        this.aiRequestCount = character.getAiRequestCount();
        this.imageUrl = imageUrl;
    }
}
