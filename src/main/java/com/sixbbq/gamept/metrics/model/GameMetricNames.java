package com.sixbbq.gamept.metrics.model;

public class GameMetricNames {

    // 캐릭터 검색 관련 메트릭
    public static final String CHARACTER_SEARCH_TIMER = "app.game.character.search.time";
    public static final String CHARACTER_SEARCH_COUNTER = "app.game.character.search.count";

    // 게임별 API 호출 메트릭
    public static final String MAPLE_API_COUNTER = "app.game.maple.api.count";
    public static final String DNF_API_COUNTER = "app.game.dnf.api.count";
    public static final String LOSTARK_API_COUNTER = "app.game.lostark.api.count";
    public static final String GAME_API_TIMER = "app.game.api.time";

    // 캐릭터 데이터 관련 메트릭
    public static final String CHARACTER_OPERATION_COUNTER = "app.game.character.operation.count";
    public static final String POPULAR_SEARCH_COUNTER = "app.game.popular.search.count";
    public static final String SERVER_SEARCH_COUNTER = "app.game.server.search.count";

    // 캐시 관련 메트릭
    public static final String CACHE_COUNTER = "app.game.cache.count";

    // 데이터베이스 관련 메트릭
    public static final String DB_QUERY_TIMER = "app.game.db.query.time";
    public static final String DB_CONNECTION_POOL_GAUGE = "app.game.db.connection.pool";

    // 사용자 활동 메트릭
    public static final String ACTIVE_USERS_GAUGE = "app.game.active.users";
    public static final String SESSION_DURATION_COUNTER = "app.game.session.duration.count";

    // 에러 관련 메트릭
    public static final String GAME_API_ERROR_COUNTER = "app.game.api.error.count";
}