package com.sixbbq.gamept.api.dnf.dto.creature;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Artifact {
    private String itemId;
    private String itemImage;
    private String itemName;
    private Integer itemAvailableLevel;
    private String itemRarity;
}
