package com.sixbbq.gamept.api.dnf.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class SpecCheckResponseDTO {
    private Map<String, Boolean> specCheckResult;
    private List<DetailSpecCheck> details;
}
