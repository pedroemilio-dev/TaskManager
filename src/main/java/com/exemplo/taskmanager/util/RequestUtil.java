package com.exemplo.taskmanager.util;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtil {
    public static String getClientIp(HttpServletRequest request) {
        String header = request.getHeader("X-Forwarded-For");

        if(header == null) {
            return request.getRemoteAddr();
        }

        return header.split(",")[0].trim();
    }
}
