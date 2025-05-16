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

    private final DFService DFService;

    /**
     * 1. 던파 캐릭터 검색 API
     *
     * [요청 방식]
     * GET /api/df/search?server={serverId}&name={characterName}
     *
     * @param server 서버 ID (영문 식별자, 아래 목록 참조)
     * @param name   캐릭터 이름
     * @return       캐릭터 ID, 요약된 정보 (JSON)
     *
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
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchCharacter(@RequestParam String server, @RequestParam String name) {
        return ResponseEntity.ok(DFService.searchCharacter(server, name));
    }
}
