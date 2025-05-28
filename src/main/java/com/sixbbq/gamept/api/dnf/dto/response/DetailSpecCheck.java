package com.sixbbq.gamept.api.dnf.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class DetailSpecCheck {
    private String userName;
    private Map<String, Boolean> detailCheckResult;
}
