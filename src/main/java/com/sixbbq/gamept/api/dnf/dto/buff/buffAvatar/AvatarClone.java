package com.sixbbq.gamept.api.dnf.dto.buff.buffAvatar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AvatarClone {
    private String itemName;
}
