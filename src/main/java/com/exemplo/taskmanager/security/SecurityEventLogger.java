package com.exemplo.taskmanager.security;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class SecurityEventLogger {
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final Logger log = LoggerFactory.getLogger("SECURITY");

    private void send(String event, String user, String ip, String resource) {
        String json = String.format("{\"time\":\"%s\",\"event\":\"%s\",\"user\":\"%s\",\"ip\":\"%s\",\"resource\":\"%s\"}",
        Instant.now(), event, user, ip, resource);

        log.warn(json);
        
        kafkaTemplate.send("spring-logs", json);
    }

    public void logLoginSuccess(String ip) {
        send("LOGIN_SUCCESS", "unknown", ip, "");
    }

    public void logFailedLogin(String ip) {
        send("FAILED_LOGIN", "unknown", ip, "");
    }

    public void logUserEnumeration(String ip) {
        send("USER_ENUMERATION", "unknown", ip, "");
    }

    public void logRegisterSuccess(String ip) {
        send("REGISTER_SUCCESS", "unknown", ip, "");
    }

    public void logRegisterFlood(String ip) {
        send("REGISTER_FLOOD", "unknown", ip, "");
    }

    public void logTokenRefreshed(String ip) {
        send("TOKEN_REFRESHED", "unknown", ip, "");
    }

    public void logRefreshTokenReuse(String ip) {
        send("REFRESH_TOKEN_REUSE", "unknown", ip, "");
    }

    public void logInvalidRefreshToken(String ip) {
        send("INVALID_REFRESH_TOKEN", "unknown", ip, "");
    }

    public void logRefreshTokenExpired(String ip) {
        send("REFRESH_TOKEN_EXPIRED", "unknown", ip, "");
    }

    public void logInvalidToken(String ip) {
        send("INVALID_TOKEN", "unknown", ip, "");
    }

    public void logBlacklistedToken(String ip) {
        send("BLACKLISTED_TOKEN", "unknown", ip, "");
    }

    public void logPasswordChangeFailed(String ip) {
        send("PASSWORD_CHANGE_FAILED", "unknown", ip, "");
    }

    public void logAccessDenied(String ip) {
        send("ACCESS_DENIED", "unknown", ip, "");
    }

    public void logUnauthorizedRead(String ip) {
        send("UNAUTHORIZED_READ", "unknown", ip, "");
    }

    public void logUnauthorizedWrite(String ip) {
        send("UNAUTHORIZED_WRITE", "unknown", ip, "");
    }

    public void logUnauthorizedDelete(String ip) {
        send("UNAUTHORIZED_DELETE", "unknown", ip, "");
    }
}
