package com.sixbbq.gamept.api.dnf.dto.buff.buffEquip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sixbbq.gamept.api.dnf.dto.skill.SkillOption;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuffSkillInfo {
    private String name;
    private SkillOption option;
}
