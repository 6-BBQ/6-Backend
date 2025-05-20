package com.sixbbq.gamept.api.dnf.dto.equip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Equip {
    private String slotName;
    private String itemName;
    private String itemRarity;
    private String setItemName;
    private String reinforce;
    private String itemGradeName;
    private Enchant enchant;
    private List<Status> status;
    private String amplificationName;
    private FusionOption fusionOption;
    private List<Tune> tune;
    private UpgradeInfo upgradeInfo;

}
