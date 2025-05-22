package com.sixbbq.gamept.api.dnf.dto.creature;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Creature {
    private String itemId;
    private String itemImage;
    private String itemName;
    private String itemRarity;
    private List<Artifact> artifact;
}
