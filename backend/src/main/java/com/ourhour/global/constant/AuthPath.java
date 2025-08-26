package com.ourhour.global.constant;

public final class AuthPath {

    private AuthPath() {}

    public static final String[] PUBLIC_URLS = {
        "/api/auth/check-email",
        "/api/auth/signup",
        "/api/auth/signin",
        "/api/auth/oauth-signin",
            "/api/auth/oauth-signin/extra-info",
        "/api/auth/email-verification",
        "/api/auth/token",
        "/api/auth/password-reset"
    };

    public static final String[] SWAGGER_URLS = {
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",
            "/webjars"
    };

    public static final String[] STOMP_URLS = {
            "/ws-stomp"
    };

    public static final String[] MONITORING_URLS = {
            "/actuator"
    };

    public static final String[] NOTIFICATION_URLS = {
            "/api/notifications/stream"
    };

}
