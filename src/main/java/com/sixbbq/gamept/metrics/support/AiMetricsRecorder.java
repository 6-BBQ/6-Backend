package com.sixbbq.gamept.metrics.support;

import com.sixbbq.gamept.metrics.model.AiMetricNames;
import com.sixbbq.gamept.metrics.model.MetricTags;
import com.sixbbq.gamept.metrics.recorder.CounterRecorder;
import com.sixbbq.gamept.metrics.recorder.TimerRecorder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiMetricsRecorder {

    private final CounterRecorder counter;
    private final TimerRecorder timer;

    @PostConstruct
    public void init() {
        System.out.println("🤖 AiMetricsRecorder 초기화 시작");
        // 미리 모든 조합 한 번씩 초기화 (0으로 등록됨)
        counterChatRequestSuccess();
        counterChatRequestFailure("API 오류");
        counterTokenUsage("request", 0);
        counterTokenUsage("response", 0);
        System.out.println("🤖 AiMetricsRecorder 초기화 완료");
    }

    // AI 채팅 요청 성공 카운터
    public void counterChatRequestSuccess() {
        System.out.println("✅ AI 채팅 요청 성공 메트릭 기록");
        counter.increment(AiMetricNames.AI_CHAT_REQUEST_COUNTER,
                MetricTags.STATUS, "success",
                MetricTags.REASON, "정상 응답");
    }

    // AI 채팅 요청 실패 카운터 (상세 분류)
    public void counterChatRequestFailure(String reason) {
        System.out.println("❌ AI 채팅 요청 실패 메트릭 기록 - reason: " + reason);
        counter.increment(AiMetricNames.AI_CHAT_REQUEST_COUNTER,
                MetricTags.STATUS, "failure",
                MetricTags.REASON, reason);
    }
    
    // 편의 메서드들
    public void counterChatLimitExceeded(String limitType) {
        counterChatRequestFailure("한도 초과: " + limitType);
    }
    
    public void counterChatApiError(String errorType) {
        counterChatRequestFailure("API 오류: " + errorType);
    }
    
    public void counterChatValidationError(String validationType) {
        counterChatRequestFailure("검증 실패: " + validationType);
    }

    // AI 토큰 사용량 카운터
    public void counterTokenUsage(String tokenType, int tokenCount) {
        System.out.println("🪙 AI 토큰 사용량 메트릭 기록 - type: " + tokenType + ", count: " + tokenCount);
        for (int i = 0; i < tokenCount; i++) {
            counter.increment(AiMetricNames.AI_CHAT_TOKEN_COUNTER,
                    "token_type", tokenType);
        }
    }

    // AI 응답 시간 측정 시작
    public io.micrometer.core.instrument.Timer.Sample startChatResponseTimer() {
        return timer.start();
    }
    
    // AI 응답 시간 측정 종료
    public void stopChatResponseTimer(io.micrometer.core.instrument.Timer.Sample sample) {
        timer.stop(sample, AiMetricNames.AI_CHAT_RESPONSE_TIMER);
    }
}
