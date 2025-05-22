package com.sixbbq.gamept.api.dnf.dto.buff.buffCreature;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuffCreature {
    private String itemId;
    private String itemImage;
    private String itemName;
    private String itemRarity;
}
