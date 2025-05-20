package com.sixbbq.gamept.api.dnf.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sixbbq.gamept.api.dnf.dto.avatar.Avatar;
import com.sixbbq.gamept.api.dnf.dto.equip.Equip;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class DFCharacterResponseDTO {

    private String characterId;
    private String characterName;
    private String imageUrl;
    private String serverId;
    private String adventureName;
    private String guildName;
    private LocalDateTime lastUpdated;
    private List<Equip> equipment;
    private List<Avatar> avatar;
}
