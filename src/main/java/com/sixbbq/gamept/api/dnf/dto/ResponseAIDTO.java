package com.sixbbq.gamept.api.dnf.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
// AI의 응답 데이터를 가져오는 클래스
public class ResponseAIDTO {
    private boolean success;
    private String answer;
    private String message;
    private String limitMessage;
    private int aiRequestCount;
}
