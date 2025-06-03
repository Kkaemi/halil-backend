package com.example.halil.auth.component;

import com.example.halil.auth.domain.AuthToken;
import com.example.halil.auth.domain.AuthTokenFactory;
import com.example.halil.auth.domain.UserInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthTokenFactory authTokenFactory;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        AuthToken authToken = authTokenFactory.parseAccessToken(token);

        if (!authToken.isValidFromNow(Instant.now())) {
            filterChain.doFilter(request, response);
            return;
        }

        UserInfo userInfo = authToken.getUserInfo();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userInfo.userId(), null, List.of(new SimpleGrantedAuthority(userInfo.role()))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
