package com.sixbbq.gamept.api.dnf.dto.buffAvatar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AvatarEmblem {
    private int slotNo;
    private String slotColor;
    private String itemName;
    private String itemRarity;
}
