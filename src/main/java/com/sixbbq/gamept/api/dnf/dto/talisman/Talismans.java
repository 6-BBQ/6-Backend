package com.sixbbq.gamept.api.dnf.dto.talisman;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Talismans {
    private Talisman talisman;
    private List<Runes> runes;
}
