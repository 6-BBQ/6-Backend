package com.sixbbq.gamept.api.dnf.dto.flag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.aot.generate.GeneratedMethod;

import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Flag {
    private String itemId;
    private String itemImage;
    private String itemName;
    private String itemRarity;
    private Integer reinforce;
    private List<ReinforceStatus> reinforceStatus;
    private List<Gems> gems;

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReinforceStatus {
        private String name;
        private double value;
    }
}
