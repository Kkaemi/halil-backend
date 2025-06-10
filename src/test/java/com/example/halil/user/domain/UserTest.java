package com.example.halil.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.halil.user.domain.exception.LoginException;
import com.example.halil.user.domain.exception.PasswordReusedException;
import com.example.halil.user.domain.exception.UserStatusException;
import com.example.halil.user.infra.BCryptPassword;
import com.example.halil.user.infra.PasswordFactoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    private final PasswordFactory passwordFactory;

    public UserTest() {
        this.passwordFactory = new PasswordFactoryImpl();
    }

    @Test
    @DisplayName("유저 생성")
    void create_user() {
        // given
        String email = "thisisemail@email.com";
        String rawPassword = "password!123";

        // when
        User sut = new User(email, passwordFactory.createPassword(rawPassword));

        // then
        assertThat(sut.getEmail()).isEqualTo(email);
        assertThat(passwordFactory.createPassword(rawPassword).matches(sut.getEncodedPassword())).isTrue();
        assertThat(sut.getRole()).isEqualTo(UserRole.ROLE_USER);
        assertThat(sut.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("비밀번호 변경시 같은 비밀번호로 변경할 수 없다")
    void password_can_not_reused() {
        // given
        String email = "thisisemail@email.com";
        String rawPassword = "password!123";
        User sut = new User(email, passwordFactory.createPassword(rawPassword));

        // when and then
        assertThatThrownBy(() -> sut.updatePassword(passwordFactory.createPassword(rawPassword)))
                .isInstanceOf(PasswordReusedException.class);
    }

    @Test
    @DisplayName("회원 탈퇴")
    void withdraw() {
        // given
        String email = "thisisemail@email.com";
        String rawPassword = "password!123";
        User sut = new User(email, passwordFactory.createPassword(rawPassword));

        // when
        sut.withdraw();

        // then
        assertThat(sut.getUserStatus()).isEqualTo(UserStatus.DELETED);
    }

    @Test
    @DisplayName("비밀번호가 다르면 로그인 불가")
    void password_mismatch() {
        // given
        String email = "thisisemail@email.com";
        String rawPassword = "password!123";
        User sut = new User(email, passwordFactory.createPassword(rawPassword));

        // when and then
        assertThatThrownBy(() -> sut.login(new BCryptPassword("wrongPassword!123")))
                .isInstanceOf(LoginException.class);
    }

    @Test
    @DisplayName("탈퇴한 회원은 로그인 불가")
    void withdrawn_user_can_not_login() {
        // given
        String email = "thisisemail@email.com";
        String rawPassword = "password!123";
        User sut = new User(email, passwordFactory.createPassword(rawPassword));

        // when
        sut.withdraw();

        // when and then
        assertThatThrownBy(() -> sut.login(passwordFactory.createPassword(rawPassword)))
                .isInstanceOf(UserStatusException.class);
    }
}
