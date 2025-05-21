package com.sixbbq.gamept.api.dnf.dto.buff.buffEquip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sixbbq.gamept.api.dnf.dto.buff.buffAvatar.BuffAvatar;
import com.sixbbq.gamept.api.dnf.dto.buff.buffCreature.BuffCreature;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuffSkill {
    private BuffSkillInfo skillInfo;
    private List<BuffEquipment> equipment;
    private List<BuffAvatar> avatar;
    private List<BuffCreature> creature;
}
