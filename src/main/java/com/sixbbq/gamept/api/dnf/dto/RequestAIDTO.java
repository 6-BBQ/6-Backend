package com.sixbbq.gamept.api.dnf.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class RequestAIDTO {
    private String query;
    private String jwtToken;
    private DFCharacterInfoResponseAIDTO characterData;
    private List<String> beforeQuestionList;
    private List<String> beforeResponseList;

    protected RequestAIDTO() {}

    public RequestAIDTO(String query, String jwtToken, DFCharacterInfoResponseAIDTO characterData, List<String> beforeQuestionList, List<String> beforeResponseList) {
        this.query = query;
        this.jwtToken = jwtToken;
        this.characterData = characterData;
        this.beforeQuestionList = beforeQuestionList;
        this.beforeResponseList = beforeResponseList;
    }
}
