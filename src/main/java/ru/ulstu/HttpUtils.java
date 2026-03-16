package ru.ulstu;

import jakarta.servlet.http.HttpServletRequest;

public class HttpUtils {
    public static String getUserIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
