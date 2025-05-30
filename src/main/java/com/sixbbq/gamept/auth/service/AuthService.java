package com.sixbbq.gamept.auth.service;

import com.sixbbq.gamept.auth.dto.LoginDto;
import com.sixbbq.gamept.auth.dto.TokenDto;
import com.sixbbq.gamept.auth.dto.TokenRequestDto;
import com.sixbbq.gamept.auth.entity.Member;
import com.sixbbq.gamept.auth.entity.RefreshToken;
import com.sixbbq.gamept.auth.repository.MemberRepository;
import com.sixbbq.gamept.auth.repository.RefreshTokenRepository;
import com.sixbbq.gamept.jwt.JwtTokenProvider;
import com.sixbbq.gamept.metrics.support.AuthMetricsRecorder;
import io.micrometer.core.instrument.Timer;
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
    private final AuthMetricsRecorder authMetricsRecorder;

    @Transactional
    public TokenDto login(LoginDto loginDto) {
        Timer.Sample sample = authMetricsRecorder.startLoginTimer();  // 🆕 시간 측정 시작

        try {
        // 회원 정보 조회
            Member member = memberRepository.findById(loginDto.getUserId())
                    .orElse(null);
                    if (member == null) {
                        authMetricsRecorder.counterLoginFailure("가입되지 않은 아이디");  // 🆕 실패 기록
                        throw new IllegalArgumentException("가입되지 않은 아이디입니다.");
                    }

            // 비밀번호 검증
            if (!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
                System.out.println("❌ 잘못된 비밀번호 로직 진입");
                authMetricsRecorder.counterLoginFailure("잘못된 비밀번호");
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
            boolean matched = passwordEncoder.matches(loginDto.getPassword(), member.getPassword());
            System.out.println("비밀번호 일치 여부: " + matched);

            if (!matched) {
                authMetricsRecorder.counterLoginFailure("잘못된 비밀번호");
                throw new IllegalArgumentException("잘못된 비밀번호입니다.");
            }

            authMetricsRecorder.counterLoginSuccess();  // 🆕 성공 기록

            return tokenDto;
        } catch (IllegalArgumentException e) {
            throw e;
        } finally {
            authMetricsRecorder.stopLoginTimer(sample, "status", "completed");  // 🆕 시간 측정 종료
        }

    }

    // 로그아웃
    @Transactional
    public void logout(String accessToken) {
        // Access Token에서 userId 추출
        String userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        // 해당 사용자의 RefreshToken 삭제
        refreshTokenRepository.deleteByUserId(userId);

        authMetricsRecorder.counterLogout();  // 🆕 로그아웃 기록
    }


    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        Timer.Sample sample = authMetricsRecorder.startTokenReissueTimer();  // 🆕 시간 측정 시작

        try{
            // Refresh Token 검증
            if (!jwtTokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
                authMetricsRecorder.counterTokenReissueFailure("Refresh Token이 유효하지 않음");  // 🆕 실패 기록
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

            authMetricsRecorder.counterTokenReissueSuccess();

            return tokenDto;
        } finally {
            authMetricsRecorder.stopTokenReissueTimer(sample, "status", "completed");  // 🆕 시간 측정 종료
        }
    }
}