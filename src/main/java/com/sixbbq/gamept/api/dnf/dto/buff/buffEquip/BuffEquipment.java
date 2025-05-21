package com.sixbbq.gamept.api.dnf.dto.buff.buffEquip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuffEquipment {
    private String slotName;
    private String itemName;
    private String itemType;
    private String itemTypeDetail;
    private int itemAvailableLevel;
    private String itemRarity;
    private String setItemName;
    private int reinforce;
    private String amplificationName;
    private int refine;
}
