package com.example.halil.exception;

import com.example.halil.user.domain.exception.EmailDuplicateException;
import com.example.halil.user.domain.exception.PasswordMismatchException;
import com.example.halil.user.domain.exception.PasswordReusedException;
import com.example.halil.user.domain.exception.UserStatusException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionController extends ResponseEntityExceptionHandler {

    private final SensitiveFieldMasker sensitiveFieldMasker;

    @ExceptionHandler(ApiException.class)
    ResponseEntity<CommonErrorResponse> handleApiException(
            ApiException e,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(e.getHttpStatus()).body(new CommonErrorResponse(
                e.getHttpStatus().value(),
                e.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        ));
    }

    @ExceptionHandler({
            PasswordMismatchException.class,
            PasswordReusedException.class,
            UserStatusException.class,
            EmailDuplicateException.class
    })
    ResponseEntity<CommonErrorResponse> handleUserDomainException(
            Exception e,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CommonErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        ));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<CommonErrorResponse> handleException(
            Exception e,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String requestId = response.getHeader("X-Request-Id");
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String queryString = request.getQueryString();
        String headers = Collections.list(request.getHeaderNames()).stream()
                .map(name -> name + "=" + request.getHeader(name))
                .collect(Collectors.joining(", "));

        String body = "[unavailable]";
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            body = sensitiveFieldMasker.maskJsonString(wrapper.getContentAsString());
        }

        log.error("""
                Request ID: [{}] Request error occurred:
                - URI: {} [{}]
                - Query: {}
                - Headers: {}
                - Body: {}
                - Exception Message: {}
                """, requestId, uri, method, queryString, headers, body, e.getMessage(), e);

        return ResponseEntity.internalServerError().body(new CommonErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        ));
    }
}
