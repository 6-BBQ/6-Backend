package com.sixbbq.gamept.api.dnf.dto.buff.buffAvatar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuffAvatar {
    private String slotName;
    private String itemName;
    private String itemRarity;
    private AvatarClone clone;
    private String optionAbility;
    private List<AvatarEmblem> emblems;
}
