package com.sixbbq.gamept.api.dnf.dto.equip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpgradeInfo {
    private String itemName;
    private String itemRarity;
    private String setItemName;
    private Integer setPoint;
}
