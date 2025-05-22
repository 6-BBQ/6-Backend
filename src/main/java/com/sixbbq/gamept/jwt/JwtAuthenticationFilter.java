package com.sixbbq.gamept.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixbbq.gamept.auth.dto.TokenDto;
import com.sixbbq.gamept.auth.entity.RefreshToken;
import com.sixbbq.gamept.auth.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = resolveToken(request);

        if (StringUtils.hasText(jwt)) {
            try {
                // 토큰 검증
                if (jwtTokenProvider.validateToken(jwt)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JwtException e) {
                if (e.getMessage().equals("Expired JWT Token")) {
                    // 토큰 만료 시 자동 갱신 시도
                    String newToken = tryRefreshToken(jwt);
                    if (newToken != null) {
                        // 새 토큰으로 인증 설정
                        Authentication authentication = jwtTokenProvider.getAuthentication(newToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        // 응답 헤더에 새 토큰 추가
                        response.setHeader("New-Access-Token", newToken);
                        response.setHeader("Token-Refreshed", "true");
                    } else {
                        // refreshToken도 만료된 경우
                        sendUnauthorizedResponse(response, "All tokens expired");
                        return;
                    }
                } else {
                    sendUnauthorizedResponse(response, e.getMessage());
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String tryRefreshToken(String expiredAccessToken) {
        try {
            // 만료된 토큰에서 userId 추출
            String userId = jwtTokenProvider.getUserIdFromExpiredToken(expiredAccessToken);

            // DB에서 refreshToken 조회
            Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findById(userId);

            if (refreshTokenOpt.isPresent()) {
                String refreshToken = refreshTokenOpt.get().getToken();

                // refreshToken 유효성 검사
                if (jwtTokenProvider.validateToken(refreshToken)) {
                    // 새 accessToken 생성
                    TokenDto newTokenDto = jwtTokenProvider.generateTokenDto(userId);

                    // 새 refreshToken으로 DB 업데이트
                    refreshTokenOpt.get().updateToken(newTokenDto.getRefreshToken());
                    refreshTokenRepository.save(refreshTokenOpt.get());

                    return newTokenDto.getAccessToken();
                }
            }
        } catch (Exception e) {
            // 갱신 실패
        }
        return null;
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("success", false);
        errorDetails.put("message", message);

        new ObjectMapper().writeValue(response.getOutputStream(), errorDetails);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}