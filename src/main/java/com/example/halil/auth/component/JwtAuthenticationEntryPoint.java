package com.example.halil.auth.component;

import com.example.halil.exception.CommonErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

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
        int httpStatusCode = HttpStatus.UNAUTHORIZED.value();

        CommonErrorResponse commonErrorResponse = new CommonErrorResponse(
                httpStatusCode, authException.getMessage(), LocalDateTime.now(), path
        );

        response.setContentType("application/json");
        response.setStatus(httpStatusCode);
        response.getWriter().write(objectMapper.writeValueAsString(commonErrorResponse));
    }
}
