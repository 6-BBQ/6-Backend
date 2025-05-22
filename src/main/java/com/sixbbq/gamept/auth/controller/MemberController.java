package com.sixbbq.gamept.auth.controller;

import com.sixbbq.gamept.auth.dto.LoginDto;
import com.sixbbq.gamept.auth.dto.SignupDto;
import com.sixbbq.gamept.auth.dto.TokenDto;
import com.sixbbq.gamept.auth.dto.TokenRequestDto;
import com.sixbbq.gamept.auth.entity.Member;
import com.sixbbq.gamept.auth.service.AuthService;
import com.sixbbq.gamept.auth.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AuthService authService;

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
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        Map<String, Object> response = new HashMap<>();

        try {
            // JWT 토큰 발급
            TokenDto tokenDto = authService.login(loginDto);

            // 사용자 정보 조회
            Member member = memberService.findById(loginDto.getUserId());

            response.put("message", "로그인 성공");
            response.put("token", tokenDto);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "로그인 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // 토큰 재발급 API
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        Map<String, Object> response = new HashMap<>();

        try {
            TokenDto tokenDto = authService.reissue(tokenRequestDto);

            response.put("success", true);
            response.put("message", "토큰 재발급 성공");
            response.put("token", tokenDto);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "토큰 재발급 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
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
    public ResponseEntity<?> getCurrentMember() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Spring Security Context에서 인증 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

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