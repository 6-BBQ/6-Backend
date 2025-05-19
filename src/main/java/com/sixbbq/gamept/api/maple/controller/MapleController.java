package com.sixbbq.gamept.api.maple.controller;

import com.sixbbq.gamept.api.maple.service.MapleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maple")
@RequiredArgsConstructor
@Tag(name = "Maple API")
public class MapleController {

    private final MapleService mapleService;

    /**
     * 1. 메이플스토리 캐릭터 검색 API (ocid 조회)
     * GET /api/maple/search?name={characterName}
     */
    @Operation(summary = "캐릭터 검색", description = "캐릭터명을 입력하면 ocid 및 기본 캐릭터 정보를 반환")
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

    @Operation(summary = "ocid로 캐릭터 상세 조회", description = "캐릭터의 ocid를 통해 상세 정보를 조회")
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

    @Operation(summary = "본캐 기준 캐릭터 그룹 조회",
            description = "입력한 representativeName(대표 캐릭터명)을 기준으로 부캐를 포함한 모든 캐릭터 정보를 반환")
    @GetMapping("/group")
    public ResponseEntity<?> getCharacterGroup(@RequestParam String representativeName) {
        return ResponseEntity.ok(mapleService.findCharactersByRepresentative(representativeName));
    }
}