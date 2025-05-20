package com.sixbbq.gamept.api.dnf.dto.equip;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class Active {
    private String explain;
    private String buffExplain;
    private List<Status> status;
    private SetPoint setPoint;

    public static class SetPoint {
        private Integer current;
        private Integer min;
        private Integer max;
    }
}
