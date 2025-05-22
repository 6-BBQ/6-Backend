package com.sixbbq.gamept.auth.service;

import com.sixbbq.gamept.auth.dto.LoginDto;
import com.sixbbq.gamept.auth.dto.TokenDto;
import com.sixbbq.gamept.auth.dto.TokenRequestDto;
import com.sixbbq.gamept.auth.entity.Member;
import com.sixbbq.gamept.auth.entity.RefreshToken;
import com.sixbbq.gamept.auth.repository.MemberRepository;
import com.sixbbq.gamept.auth.repository.RefreshTokenRepository;
import com.sixbbq.gamept.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenDto login(LoginDto loginDto) {
        // 회원 정보 조회
        Member member = memberRepository.findById(loginDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 아이디입니다."));

        // 비밀번호 검증
        if (!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        // 토큰 생성
        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(member.getUserId());

        // RefreshToken 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(member.getUserId())
                .token(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.findById(member.getUserId())
                .ifPresentOrElse(
                        token -> token.updateToken(tokenDto.getRefreshToken()),
                        () -> refreshTokenRepository.save(refreshToken)
                );

        return tokenDto;
    }

    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        // Refresh Token 검증
        if (!jwtTokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new IllegalArgumentException("Refresh Token이 유효하지 않습니다.");
        }

        // Access Token에서 Member ID 가져오기
        String userId = jwtTokenProvider.getAuthentication(tokenRequestDto.getAccessToken())
                .getName();

        // 저장소에서 Member ID를 기반으로 Refresh Token 가져오기
        RefreshToken refreshToken = refreshTokenRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("로그아웃된 사용자입니다."));

        // Refresh Token 검증
        if (!refreshToken.getToken().equals(tokenRequestDto.getRefreshToken())) {
            throw new IllegalArgumentException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 새 토큰 생성
        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(userId);

        // 저장소 정보 업데이트
        refreshToken.updateToken(tokenDto.getRefreshToken());

        return tokenDto;
    }
}