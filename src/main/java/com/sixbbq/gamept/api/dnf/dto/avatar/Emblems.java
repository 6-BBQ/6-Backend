package com.sixbbq.gamept.api.dnf.dto.avatar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Emblems {
    private Integer slotNo;
    private String itemName;
    private String itemRarity;
}
