package com.sixbbq.gamept.api.dnf.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sixbbq.gamept.api.dnf.dto.avatar.Avatar;
import com.sixbbq.gamept.api.dnf.dto.creature.Creature;
import com.sixbbq.gamept.api.dnf.dto.equip.Equip;
import com.sixbbq.gamept.api.dnf.dto.equip.SetItemInfo;
import com.sixbbq.gamept.api.dnf.dto.flag.Flag;
import com.sixbbq.gamept.api.dnf.dto.skill.Skill;
import com.sixbbq.gamept.api.dnf.dto.talisman.Talismans;
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
    private Integer level;
    private String imageUrl;
    private String serverId;
    private String adventureName;
    private String guildName;
    private String jobName;
    private String jobGrowName;
    private String fame;
    private LocalDateTime lastUpdated;
    private List<Equip> equipment;
    private List<SetItemInfo> setItemInfo;
    private List<Avatar> avatar;
    private Creature creature;
    private Flag flag;
    private List<Talismans> talismans;
    private Skill skill;
}
