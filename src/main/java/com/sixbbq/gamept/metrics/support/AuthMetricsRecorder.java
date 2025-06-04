package com.sixbbq.gamept.metrics.support;

import com.sixbbq.gamept.metrics.model.AuthMetricNames;
import com.sixbbq.gamept.metrics.model.MetricTags;
import com.sixbbq.gamept.metrics.recorder.CounterRecorder;
import com.sixbbq.gamept.metrics.recorder.TimerRecorder;
import com.sixbbq.gamept.metrics.recorder.GaugeRecorder;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AuthMetricsRecorder {

    private final CounterRecorder counter;
    private final TimerRecorder timer;
    private final GaugeRecorder gauge;

    public AuthMetricsRecorder(CounterRecorder counter, TimerRecorder timer, GaugeRecorder gauge) {
        this.counter = counter;
        this.timer = timer;
        this.gauge = gauge;
    }

    // 로그인 타이머 시작
    public Timer.Sample startLoginTimer() {
        return timer.start();
    }

    // 로그인 타이머 종료
    public void stopLoginTimer(Timer.Sample sample, String... tags) {
        timer.stop(sample, AuthMetricNames.AUTH_LOGIN_TIMER, tags);
    }

    // 로그인 성공 카운터
    public void counterLoginSuccess() {
        counter.increment(AuthMetricNames.AUTH_LOGIN_COUNTER,
                MetricTags.STATUS, "success",
                MetricTags.REASON, "정상 로그인");
    }

    // 로그인 실패 카운터
    public void counterLoginFailure(String reason) {
        counter.increment(AuthMetricNames.AUTH_LOGIN_COUNTER,
                MetricTags.STATUS, "failure",
                MetricTags.REASON, reason);
    }

    // 회원가입 성공 카운터
    public void counterSignupSuccess() {
        System.out.println("✅ 회원가입 성공 메트릭 기록 시작");
        counter.increment(AuthMetricNames.AUTH_SIGNUP_COUNTER,
                MetricTags.STATUS, "success",
                MetricTags.REASON, "정상 회원가입");
        System.out.println("✅ 회원가입 성공 메트릭 기록 완료");
    }

    // 회원가입 실패 카운터
    public void counterSignupFailure(String reason) {
        System.out.println("❌ 회원가입 실패 메트릭 기록 시작 - reason: " + reason);
        counter.increment(AuthMetricNames.AUTH_SIGNUP_COUNTER,
                MetricTags.STATUS, "failure",
                MetricTags.REASON, reason);
        System.out.println("❌ 회원가입 실패 메트릭 기록 완료");
    }


    // 로그아웃 카운터
    public void counterLogout() {
        counter.increment(AuthMetricNames.AUTH_LOGOUT_COUNTER, MetricTags.STATUS, "success");
    }

    // 토큰 재발급 타이머 시작
    public Timer.Sample startTokenReissueTimer() {
        return timer.start();
    }

    // 토큰 재발급 타이머 종료
    public void stopTokenReissueTimer(Timer.Sample sample, String... tags) {
        timer.stop(sample, AuthMetricNames.AUTH_TOKEN_REISSUE_TIMER, tags);
    }

    // 토큰 재발급 성공 카운터
    public void counterTokenReissueSuccess() {
        counter.increment(AuthMetricNames.AUTH_TOKEN_REISSUE_COUNTER, MetricTags.STATUS, "success");
    }

    // 토큰 재발급 실패 카운터
    public void counterTokenReissueFailure(String reason) {
        counter.increment(AuthMetricNames.AUTH_TOKEN_REISSUE_COUNTER,
                MetricTags.STATUS, "failure",
                MetricTags.REASON, reason);
    }

    // 활성 세션 수 게이지 등록
    public void registerActiveSessionsGauge(AtomicInteger activeSessions) {
        gauge.registerListSize(AuthMetricNames.AUTH_ACTIVE_SESSIONS_GAUGE,
                java.util.List.of(activeSessions.get()));
    }

    @PostConstruct
    public void init() {
        // 미리 모든 조합 한 번씩 초기화 (0으로 등록됨)
        counterLoginFailure("잘못된 비밀번호");
        counterLoginSuccess();
        counterSignupFailure("이미 존재하는 아이디");
        counterSignupSuccess();
    }
}