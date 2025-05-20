package com.sixbbq.gamept.auth.controller;

import com.sixbbq.gamept.auth.dto.LoginDto;
import com.sixbbq.gamept.auth.dto.SignupDto;
import com.sixbbq.gamept.auth.entity.Member;
import com.sixbbq.gamept.auth.service.MemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupDto signupDto) {
        Map<String, Object> response = new HashMap<>();

        try {
            Member member = memberService.signup(signupDto);
            if (member != null) {
                response.put("success", true);
                response.put("message", "회원가입이 완료되었습니다.");
                response.put("userId", member.getUserId());
                response.put("nickname", member.getNickname());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("success", false);
                response.put("message", "이미 사용 중인 아이디입니다.");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            Member member = memberService.login(loginDto);
            if (member != null) {
                // 세션에 로그인 정보 저장
                session.setAttribute("LOGGED_IN_MEMBER_ID", member.getUserId());

                response.put("success", true);
                response.put("message", "로그인 성공");
                response.put("userId", member.getUserId());
                response.put("nickname", member.getNickname());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "로그인 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 로그아웃 API
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.removeAttribute("LOGGED_IN_MEMBER_ID");
        session.invalidate();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "로그아웃 되었습니다.");

        return ResponseEntity.ok(response);
    }

    // 현재 로그인한 회원 정보 조회 API
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentMember(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        String userId = (String) session.getAttribute("LOGGED_IN_MEMBER_ID");
        if (userId == null) {
            response.put("success", false);
            response.put("message", "로그인 상태가 아닙니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            Member member = memberService.findById(userId);
            if (member != null) {
                response.put("success", true);
                response.put("userId", member.getUserId());
                response.put("nickname", member.getNickname());
                response.put("createdAt", member.getCreatedAt());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "회원 정보를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "회원 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}