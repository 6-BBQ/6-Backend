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
     * 캐릭터 기본 정보 조회
     * 
     * - 메이플스토리 API를 호출하여 캐릭터 기본 정보를 가져옴.
     * - 이름, 월드, 직업, 레벨 등 캐릭터의 기본 정보를 반환.
     * - 조회한 캐릭터 정보는 DB에 자동으로 저장(계정-캐릭터 매핑용).
     * 
     * @param name 조회할 캐릭터 이름
     * @return 캐릭터 기본 정보
     */
    @GetMapping("/character")
    public ResponseEntity<?> getCharacterInfo(@RequestParam String name) {
        return ResponseEntity.ok(mapleService.getCharacterInfo(name));
    }

    /**
     * 계정에 속한 캐릭터 목록 조회
     * 
     * - DB에 저장된 정보를 기반으로 특정 계정에 속한 모든 캐릭터를 조회.
     * - 각 캐릭터의 이름, 월드, 직업 정보를 목록으로 반환.
     * 
     * @param id 조회할 계정 ID
     * @return 해당 계정에 속한 캐릭터 목록
     */
    @GetMapping("/account/characters")
    public ResponseEntity<?> getAccountCharacters(@RequestParam String id) {
        return ResponseEntity.ok(mapleService.getAccountCharacters(id));
    }
}
