package com.sixbbq.gamept.metrics.support;

import com.sixbbq.gamept.metrics.model.GameMetricNames;
import com.sixbbq.gamept.metrics.model.MetricTags;
import com.sixbbq.gamept.metrics.recorder.CounterRecorder;
import com.sixbbq.gamept.metrics.recorder.TimerRecorder;
import com.sixbbq.gamept.metrics.recorder.GaugeRecorder;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class GameMetricsRecorder {

    private final CounterRecorder counter;
    private final TimerRecorder timer;
    private final GaugeRecorder gauge;

    public GameMetricsRecorder(CounterRecorder counter, TimerRecorder timer, GaugeRecorder gauge) {
        this.counter = counter;
        this.timer = timer;
        this.gauge = gauge;
    }

    // 캐릭터 검색 관련 메트릭

    // 캐릭터 검색 타이머 시작
    public Timer.Sample startCharacterSearchTimer() {
        return timer.start();
    }

    // 캐릭터 검색 타이머 종료
    public void stopCharacterSearchTimer(Timer.Sample sample, String gameType, String status) {
        timer.stop(sample, GameMetricNames.CHARACTER_SEARCH_TIMER,
                "game_type", gameType,
                MetricTags.STATUS, status);
    }

    // 캐릭터 검색 성공
    public void counterCharacterSearchSuccess(String gameType, String server) {
        counter.increment(GameMetricNames.CHARACTER_SEARCH_COUNTER,
                "game_type", gameType,
                "server", server,
                MetricTags.STATUS, "success");
    }

    // 캐릭터 검색 실패
    public void counterCharacterSearchFailure(String gameType, String reason) {
        counter.increment(GameMetricNames.CHARACTER_SEARCH_COUNTER,
                "game_type", gameType,
                MetricTags.STATUS, "failure",
                MetricTags.REASON, reason);
    }

    // 게임별 API 호출 메트릭

    // 메이플스토리 API 호출
    public void counterMapleApiCall(String endpoint, String status) {
        counter.increment(GameMetricNames.MAPLE_API_COUNTER,
                "endpoint", endpoint,
                MetricTags.STATUS, status);
    }

    // 던전앤파이터 API 호출
    public void counterDnfApiCall(String endpoint, String status) {
        counter.increment(GameMetricNames.DNF_API_COUNTER,
                "endpoint", endpoint,
                MetricTags.STATUS, status);
    }

    // 로스트아크 API 호출
    public void counterLostArkApiCall(String endpoint, String status) {
        counter.increment(GameMetricNames.LOSTARK_API_COUNTER,
                "endpoint", endpoint,
                MetricTags.STATUS, status);
    }

    // 외부 게임 API 응답 시간
    public Timer.Sample startGameApiTimer() {
        return timer.start();
    }

    public void stopGameApiTimer(Timer.Sample sample, String gameType, String endpoint) {
        timer.stop(sample, GameMetricNames.GAME_API_TIMER,
                "game_type", gameType,
                "endpoint", endpoint);
    }

    // 캐릭터 데이터 관련 메트릭

    // 캐릭터 생성/수정
    public void counterCharacterOperation(String gameType, String operation) {
        counter.increment(GameMetricNames.CHARACTER_OPERATION_COUNTER,
                "game_type", gameType,
                "operation", operation); // create, update, delete
    }

    // 인기 검색어 메트릭
    public void counterPopularSearch(String gameType, String characterName) {
        counter.increment(GameMetricNames.POPULAR_SEARCH_COUNTER,
                "game_type", gameType,
                "character_name", characterName);
    }

    // 서버별 검색 통계
    public void counterServerSearch(String gameType, String serverName) {
        counter.increment(GameMetricNames.SERVER_SEARCH_COUNTER,
                "game_type", gameType,
                "server", serverName);
    }

    // 캐시 관련 메트릭

    // 캐시 히트
    public void counterCacheHit(String gameType, String cacheType) {
        counter.increment(GameMetricNames.CACHE_COUNTER,
                "game_type", gameType,
                "cache_type", cacheType,
                "result", "hit");
    }

    // 캐시 미스
    public void counterCacheMiss(String gameType, String cacheType) {
        counter.increment(GameMetricNames.CACHE_COUNTER,
                "game_type", gameType,
                "cache_type", cacheType,
                "result", "miss");
    }

    // 데이터베이스 관련 메트릭

    // DB 쿼리 실행 시간
    public Timer.Sample startDbQueryTimer() {
        return timer.start();
    }

    public void stopDbQueryTimer(Timer.Sample sample, String queryType, String table) {
        timer.stop(sample, GameMetricNames.DB_QUERY_TIMER,
                "query_type", queryType,
                "table", table);
    }

    // DB 연결 풀 사용률 게이지
    public void registerDbConnectionPoolUsage(AtomicInteger activeConnections, AtomicInteger maxConnections) {
        gauge.registerListSize(GameMetricNames.DB_CONNECTION_POOL_GAUGE,
                java.util.List.of(activeConnections.get()));
    }

    // 사용자 활동 메트릭

    // 활성 사용자 수
    public void registerActiveUsers(AtomicInteger activeUsers) {
        gauge.registerListSize(GameMetricNames.ACTIVE_USERS_GAUGE,
                java.util.List.of(activeUsers.get()));
    }

    // 세션 지속 시간
    public void recordSessionDuration(String gameType, long durationMinutes) {
        counter.increment(GameMetricNames.SESSION_DURATION_COUNTER,
                "game_type", gameType,
                "duration_range", getDurationRange(durationMinutes));
    }

    // 에러 관련 메트릭

    // 게임 API 에러
    public void counterGameApiError(String gameType, String errorType, String errorCode) {
        counter.increment(GameMetricNames.GAME_API_ERROR_COUNTER,
                "game_type", gameType,
                "error_type", errorType,
                "error_code", errorCode);
    }

    // Helper 메서드
    private String getDurationRange(long minutes) {
        if (minutes < 5) return "0-5min";
        if (minutes < 15) return "5-15min";
        if (minutes < 30) return "15-30min";
        if (minutes < 60) return "30-60min";
        return "60min+";
    }
}