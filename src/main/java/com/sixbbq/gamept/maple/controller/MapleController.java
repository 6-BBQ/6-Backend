package com.sixbbq.gamept.maple.controller;

import com.sixbbq.gamept.maple.service.MapleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maple")
@RequiredArgsConstructor
public class MapleController {

    private final MapleService mapleService;

    /**
     * 1. 메이플스토리 캐릭터 검색 API (ocid 조회)
     * GET /api/maple/search?name={characterName}
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchCharacter(@RequestParam String name) {
        return ResponseEntity.ok(mapleService.searchCharacter(name));
    }
    
    /**
     * 2. 메이플스토리 캐릭터 상세 정보 조회 API
     * GET /api/maple/character?ocid={ocid}
     * 
     * @param ocid 캐릭터 고유 ID
     */
    @GetMapping("/character")
    public ResponseEntity<?> getCharacterInfo(@RequestParam String ocid) {
        return ResponseEntity.ok(mapleService.getCharacterInfo(ocid));
    }
}