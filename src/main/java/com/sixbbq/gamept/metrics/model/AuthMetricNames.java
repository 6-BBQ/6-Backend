package com.sixbbq.gamept.metrics.model;

public class AuthMetricNames {

    // 로그인 관련 메트릭
    public static final String AUTH_LOGIN_TIMER = "app.auth.login.time";
    public static final String AUTH_LOGIN_COUNTER = "app_auth_login_count_total";

    // 회원가입 관련 메트릭
    public static final String AUTH_SIGNUP_COUNTER = "app_auth_signup_count_total";

    // 로그아웃 관련 메트릭
    public static final String AUTH_LOGOUT_COUNTER = "app_auth_logout_count_total";

    // 토큰 재발급 관련 메트릭
    public static final String AUTH_TOKEN_REISSUE_TIMER = "app.auth.token.reissue.time";
    public static final String AUTH_TOKEN_REISSUE_COUNTER = "app_auth_token_reissue_count_total";

    // 활성 세션 관련 메트릭
    public static final String AUTH_ACTIVE_SESSIONS_GAUGE = "app.auth.active.sessions";
}