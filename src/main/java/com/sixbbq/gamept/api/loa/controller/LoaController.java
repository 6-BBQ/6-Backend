package com.sixbbq.gamept.api.loa.controller;

import com.sixbbq.gamept.api.loa.model.dto.loaapi.response.CharacterInfoResponseDTO;
import com.sixbbq.gamept.api.loa.service.LoaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loa")
public class LoaController {
    private static final Logger log = LoggerFactory.getLogger(LoaController.class);

    private final LoaService loaService;

    public LoaController(LoaService loaService) {
        this.loaService = loaService;
    }


    // 캐릭터의 계정 단위로 캐릭목록 검색[원정대 검색]
    @GetMapping("/roster")
    public ResponseEntity<?> searchRoaster(@RequestParam String characterName) {
        log.info("/api/loa/roster : GET");
        log.info("검색 캐릭터명 : " + characterName);

        List<CharacterInfoResponseDTO> characters = loaService.getRoster(characterName);

        if (characters == null || characters.isEmpty()) {
            return ResponseEntity.ok().body("일치하는 캐릭터가 없습니다!");
        } else
            return ResponseEntity.ok().body(characters);
    }

    // 캐릭터 정보 조회
    @GetMapping("/character")
    public ResponseEntity<?> searchCharacter(@RequestParam String characterName,
                                             @RequestParam(required = false) String filter) {
        log.info("/api/loa/character : GET");
        log.info("검색 캐릭터명 : " + characterName);
        log.info("검색 정보[필터] : " + filter);

        Map<String, Object> characterInfo = loaService.getCharacterInfo(characterName, filter);

        if(characterInfo == null)
            return ResponseEntity.ok().body("일치하는 캐릭터가 없습니다!");
        else
            return ResponseEntity.ok().body(characterInfo);
    }

}
