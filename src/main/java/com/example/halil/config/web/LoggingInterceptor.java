package com.example.halil.config.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        String path = request.getRequestURI();
        String method = request.getMethod();
        String ip = request.getRemoteAddr();

        request.setAttribute("startTime", System.currentTimeMillis());

        response.addHeader("X-Request-Id", requestId);

        log.info("Request ID: [{}] Method: [{}] Path: [{}] IP: [{}]", requestId, method, path, ip);

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex
    ) throws Exception {
        String requestId = response.getHeader("X-Request-Id");
        long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;

        log.info("Request ID: [{}] Response Status: [{}] Duration: [{}ms]", requestId, response.getStatus(), duration);

        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
