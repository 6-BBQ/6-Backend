package com.sixbbq.gamept.metrics.model;

public class ApiMetricNames {

    // HTTP 요청 관련 메트릭
    public static final String API_REQUEST_TIMER = "app.api.request.time";
    public static final String API_REQUEST_COUNTER = "app.api.request.count";
    public static final String API_ERROR_COUNTER = "app.api.error.count";
    public static final String API_RESPONSE_SIZE_COUNTER = "app.api.response.size.count";
    public static final String API_CONCURRENT_REQUESTS_GAUGE = "app.api.concurrent.requests";

    // 외부 API 호출 관련 메트릭
    public static final String EXTERNAL_API_COUNTER = "app.external.api.count";
    public static final String EXTERNAL_API_TIMER = "app.external.api.time";

    // Rate Limiting 관련 메트릭
    public static final String API_RATE_LIMIT_COUNTER = "app.api.rate.limit.count";

    // 성능 관련 메트릭
    public static final String API_THROUGHPUT_GAUGE = "app.api.throughput";
    public static final String API_LATENCY_PERCENTILE = "app.api.latency.percentile";
}