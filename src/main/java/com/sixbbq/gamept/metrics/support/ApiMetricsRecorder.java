package com.sixbbq.gamept.metrics.support;

import com.sixbbq.gamept.metrics.model.ApiMetricNames;
import com.sixbbq.gamept.metrics.model.MetricTags;
import com.sixbbq.gamept.metrics.recorder.CounterRecorder;
import com.sixbbq.gamept.metrics.recorder.TimerRecorder;
import com.sixbbq.gamept.metrics.recorder.GaugeRecorder;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ApiMetricsRecorder {

    private final CounterRecorder counter;
    private final TimerRecorder timer;
    private final GaugeRecorder gauge;

    public ApiMetricsRecorder(CounterRecorder counter, TimerRecorder timer, GaugeRecorder gauge) {
        this.counter = counter;
        this.timer = timer;
        this.gauge = gauge;
    }

    // HTTP 요청 관련 메트릭

    // HTTP 요청 타이머 시작
    public Timer.Sample startRequestTimer() {
        return timer.start();
    }

    // HTTP 요청 타이머 종료
    public void stopRequestTimer(Timer.Sample sample, String method, String endpoint, String status) {
        timer.stop(sample, ApiMetricNames.API_REQUEST_TIMER,
                "method", method,
                "endpoint", endpoint,
                MetricTags.STATUS, status);
    }

    // HTTP 요청 성공 카운터
    public void counterRequestSuccess(String method, String endpoint) {
        counter.increment(ApiMetricNames.API_REQUEST_COUNTER,
                "method", method,
                "endpoint", endpoint,
                MetricTags.STATUS, "success");
    }

    // HTTP 요청 실패 카운터 (4xx, 5xx)
    public void counterRequestFailure(String method, String endpoint, String status) {
        counter.increment(ApiMetricNames.API_REQUEST_COUNTER,
                "method", method,
                "endpoint", endpoint,
                MetricTags.STATUS, "failure",
                "error_type", status);
    }

    // API 에러 카운터
    public void counterApiError(String endpoint, String errorType, String errorMessage) {
        counter.increment(ApiMetricNames.API_ERROR_COUNTER,
                "endpoint", endpoint,
                "error_type", errorType,
                "error_message", errorMessage);
    }

    // 응답 크기 메트릭
    public void recordResponseSize(String endpoint, double sizeBytes) {
        // 응답 크기를 히스토그램으로 기록
        counter.increment(ApiMetricNames.API_RESPONSE_SIZE_COUNTER,
                "endpoint", endpoint,
                "size_range", getSizeRange(sizeBytes));
    }

    // 동시 요청 수 게이지
    public void registerConcurrentRequests(AtomicInteger concurrentRequests) {
        gauge.registerListSize(ApiMetricNames.API_CONCURRENT_REQUESTS_GAUGE,
                java.util.List.of(concurrentRequests.get()));
    }

    // 외부 API 호출 메트릭

    // 외부 API 호출 성공
    public void counterExternalApiSuccess(String apiName, String endpoint) {
        counter.increment(ApiMetricNames.EXTERNAL_API_COUNTER,
                "api_name", apiName,
                "endpoint", endpoint,
                MetricTags.STATUS, "success");
    }

    // 외부 API 호출 실패
    public void counterExternalApiFailure(String apiName, String endpoint, String reason) {
        counter.increment(ApiMetricNames.EXTERNAL_API_COUNTER,
                "api_name", apiName,
                "endpoint", endpoint,
                MetricTags.STATUS, "failure",
                MetricTags.REASON, reason);
    }

    // 외부 API 응답 시간
    public Timer.Sample startExternalApiTimer() {
        return timer.start();
    }

    public void stopExternalApiTimer(Timer.Sample sample, String apiName, String endpoint) {
        timer.stop(sample, ApiMetricNames.EXTERNAL_API_TIMER,
                "api_name", apiName,
                "endpoint", endpoint);
    }

    // Rate Limiting 메트릭
    public void counterRateLimitHit(String endpoint, String clientId) {
        counter.increment(ApiMetricNames.API_RATE_LIMIT_COUNTER,
                "endpoint", endpoint,
                "client_id", clientId);
    }

    // Helper 메서드
    private String getSizeRange(double sizeBytes) {
        if (sizeBytes < 1024) return "0-1KB";
        if (sizeBytes < 10240) return "1KB-10KB";
        if (sizeBytes < 102400) return "10KB-100KB";
        if (sizeBytes < 1048576) return "100KB-1MB";
        return "1MB+";
    }
}