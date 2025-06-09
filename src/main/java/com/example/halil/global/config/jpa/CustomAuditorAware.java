package com.example.halil.global.config.jpa;

import com.example.halil.user.domain.User;
import com.example.halil.user.domain.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuditorAware implements AuditorAware<User> {

    private final UserRepository userRepository;

    @NonNull
    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        Object userId = authentication.getPrincipal();
        if (!(userId instanceof Long)) {
            return Optional.empty();
        }

        try {
            return userRepository.findById((Long) userId);
        } catch (Exception e) {
            log.error("사용자 조회 중 오류 발생", e);
            return Optional.empty();
        }
    }
}
