package com.example.halil.auth.component;

import com.example.halil.auth.exception.AuthErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

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

        log.warn("[event=authentication_failure] [status={}] [path={}] [method={}] [ip={}] [user-agent={}]",
                httpStatusCode, path, method, ip, ua
        );

        AuthErrorResponse authErrorResponse = new AuthErrorResponse(
                httpStatusCode, authException.getMessage(), LocalDateTime.now(), path
        );

        response.setContentType("application/json");
        response.setStatus(httpStatusCode);
        response.getWriter().write(objectMapper.writeValueAsString(authErrorResponse));
    }
}
