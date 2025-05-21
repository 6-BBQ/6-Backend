package com.sixbbq.gamept.api.dnf.dto.skill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SkillInfo {
    private String name;
    private Integer level;
    private String costType;
}
