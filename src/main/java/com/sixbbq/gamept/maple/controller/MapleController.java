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

    @GetMapping("/character")
    public ResponseEntity<?> getCharacterInfo(@RequestParam String name) {
        return ResponseEntity.ok(mapleService.getCharacterInfo(name));
    }

    @GetMapping("/account/characters")
    public ResponseEntity<?> getAccountCharacters(@RequestParam String id) {
        return ResponseEntity.ok(mapleService.getAccountCharacters(id));
    }
}
