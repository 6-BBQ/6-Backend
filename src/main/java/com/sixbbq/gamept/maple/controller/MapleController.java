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

    /**
     * 3. 본캐(대표캐릭터 이름) 기준으로 묶인 모든 캐릭터 조회
     * - 해당 representativeName으로 등록된 캐릭터들을 모두 가져옴
     * - 예: 본캐가 "기부"면 그 계정의 모든 캐릭터(부캐 포함)를 반환
     *
     * 예: /api/maple/group?representativeName=기부
     *
     * @param representativeName 대표 캐릭터 이름
     * @return 해당 본캐로 연결된 모든 캐릭터 목록
     */
    @GetMapping("/group")
    public ResponseEntity<?> getCharacterGroup(@RequestParam String representativeName) {
        return ResponseEntity.ok(mapleService.findCharactersByRepresentative(representativeName));
    }
}