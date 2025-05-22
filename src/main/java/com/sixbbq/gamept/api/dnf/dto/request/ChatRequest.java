package com.sixbbq.gamept.api.dnf.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatRequest {
    private String characterId;
    private String chatQuestionMessage;
    private String chatAnswerMessage;
}
