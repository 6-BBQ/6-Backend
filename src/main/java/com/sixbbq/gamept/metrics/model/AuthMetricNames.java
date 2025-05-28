package com.sixbbq.gamept.metrics.model;

public class AuthMetricNames {

    // 로그인 관련 메트릭
    public static final String AUTH_LOGIN_TIMER = "app.auth.login.time";
    public static final String AUTH_LOGIN_COUNTER = "app.auth.login.count";

    // 로그아웃 관련 메트릭
    public static final String AUTH_LOGOUT_COUNTER = "app.auth.logout.count";

    // 토큰 재발급 관련 메트릭
    public static final String AUTH_TOKEN_REISSUE_TIMER = "app.auth.token.reissue.time";
    public static final String AUTH_TOKEN_REISSUE_COUNTER = "app.auth.token.reissue.count";

    // 활성 세션 관련 메트릭
    public static final String AUTH_ACTIVE_SESSIONS_GAUGE = "app.auth.active.sessions";
}