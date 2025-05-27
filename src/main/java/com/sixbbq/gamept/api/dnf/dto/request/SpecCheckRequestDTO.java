package com.sixbbq.gamept.api.dnf.dto.request;

import com.sixbbq.gamept.api.dnf.dto.response.SpecCheckUserInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class SpecCheckRequestDTO {
    private String setItem;
    @NotBlank(message = "하나 이상의 유저명을 입력해주세요!")
    private List<SpecCheckUserInfo> checkUserList;
}
