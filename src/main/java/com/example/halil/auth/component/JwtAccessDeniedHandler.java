package com.example.halil.auth.component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";
        String role = (auth != null) ? auth.getAuthorities().toString() : "N/A";

        String path = request.getRequestURI();
        String method = request.getMethod();
        String ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");
        int httpStatusCode = HttpStatus.FORBIDDEN.value();

        log.warn("[event=access_denied] [status={}] [user={}] [role={}] [path={}] [method={}] [ip={}] [ua={}]",
                httpStatusCode, userId, role, path, method, ip, ua);

        response.setContentType("application/json");
        response.setStatus(httpStatusCode);
        response.getWriter().write(String.format("""
                {
                    "message":"access denied",
                    "statusCode":%s
                }
                """, httpStatusCode));
    }
}
