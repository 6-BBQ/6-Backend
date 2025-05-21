package com.sixbbq.gamept.api.dnf.dto.skill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sixbbq.gamept.api.dnf.dto.buff.buffEquip.BuffSkill;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Skill {
    private SkillStyle style;
    private BuffSkill buff;
}
