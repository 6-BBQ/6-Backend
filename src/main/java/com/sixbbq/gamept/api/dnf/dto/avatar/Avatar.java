package com.sixbbq.gamept.api.dnf.dto.avatar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Avatar {
    private String itemId;
    private String itemImage;
    private String slotName;
    private String itemRarity;
    private Clone clone;
    private String optionAbility;
    private List<Emblems> emblems;

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Clone {
        private String itemName;
    }
}
