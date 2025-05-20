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
public class Tune {
    private String level;
    private Integer setPoint;
    private String upgrade;
    private List<Status> status;
}
