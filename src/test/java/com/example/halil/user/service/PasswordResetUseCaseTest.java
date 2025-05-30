package com.example.halil.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.halil.user.domain.TemporaryPasswordGenerator;
import com.example.halil.user.domain.User;
import com.example.halil.user.domain.UserRepository;
import com.example.halil.user.infra.BCryptPassword;
import com.example.halil.user.infra.UserMemoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PasswordResetUseCaseTest {

    @Mock
    private MailService mailService;

    @Test
    @DisplayName("임시 비밀번호 설정하면 유저의 비밀번호가 바뀌어야 된다")
    void set_temp_password() {
        // given
        String email = "kitys2k3@naver.com";
        String rawPassword = "thisispassword!!123";
        String temporaryPassword = "8pZo$B#co4";
        User user = new User(email, new BCryptPassword(rawPassword));

        TemporaryPasswordGenerator temporaryPasswordGenerator = () -> temporaryPassword;
        UserRepository userRepository = new UserMemoryRepository();

        userRepository.save(user);

        PasswordResetUseCase sut = new PasswordResetUseCase(
                userRepository,
                temporaryPasswordGenerator,
                mailService
        );

        // when
        sut.resetPassword(email);

        // then
        assertThat(new BCryptPassword(rawPassword).matches(user.getEncodedPassword())).isFalse();
        verify(mailService, times(1)).sendPasswordResetMail(email, temporaryPassword);
    }
}
