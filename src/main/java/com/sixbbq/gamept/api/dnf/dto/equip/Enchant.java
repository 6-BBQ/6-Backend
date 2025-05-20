package com.sixbbq.gamept.api.dnf.dto.equip;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class Enchant {
    private List<Status> status;
}
