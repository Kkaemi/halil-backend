package com.example.halil.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();
        String ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");
        int httpStatusCode = HttpStatus.UNAUTHORIZED.value();

        log.warn("[event=authentication_failure] [status={}] [path={}] [method={}] [ip={}] [ua={}]",
                httpStatusCode, path, method, ip, ua);

        response.setContentType("application/json");
        response.setStatus(httpStatusCode);
        response.getWriter().write(String.format("""
                {
                    "message":"token is missing or invalid",
                    "statusCode":%s
                }
                """, httpStatusCode));
    }
}
