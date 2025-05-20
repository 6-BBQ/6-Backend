package com.sixbbq.gamept.api.dnf.dto.talisman;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Runes {
    private Integer slotNo;
    private String itemName;
}
