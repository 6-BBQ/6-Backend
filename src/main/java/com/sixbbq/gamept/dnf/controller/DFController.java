package com.sixbbq.gamept.dnf.controller;

import com.sixbbq.gamept.dnf.service.DFService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/df")
@RequiredArgsConstructor
public class DFController {

    private final DFService dfService;

    /**
     * [가능한 서버 ID 목록]
     *  - all       : 전체
     *  - cain      : 카인
     *  - diregie   : 디레지에
     *  - siroco    : 시로코
     *  - prey      : 프레이
     *  - casillas  : 카시야스
     *  - hilder    : 힐더
     *  - anton     : 안톤
     *  - bakal     : 바칼
     *  - adven     : 모험단
     */

    /**
     * 1. 던파 캐릭터 검색 API
     *
     * [요청 방식]
     * GET /api/df/search?server={serverId}&name={characterName}
     *
     * @param server 서버 ID (영문 식별자, 목록 참조) // 모험단
     * @param name   캐릭터 이름 // 모험단명
     * @return       캐릭터 ID, 요약된 정보 (JSON)
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchCharacter(@RequestParam String server, @RequestParam String name) {
        return ResponseEntity.ok(dfService.searchCharacter(server, name));
    }

    /**
     * 2. 던파 캐릭터 상세 조회 API
     *
     * [요청 방식]
     * GET /api/df/character?server={serverId}&characterId={characterId}
     *
     * @param server      서버 ID (영문 식별자)
     * @param characterId 캐릭터 ID
     * @return            이름, 레벨, 직업, 이미지 URL 등 기본 정보 (JSON)
     */
    @GetMapping("/character")
    public ResponseEntity<?> getCharacterInfo(@RequestParam String server, @RequestParam String characterId) {
        return ResponseEntity.ok(dfService.getCharacterInfo(server, characterId));
    }
}
