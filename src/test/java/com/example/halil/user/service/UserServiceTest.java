package com.example.halil.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import com.example.halil.user.domain.PasswordService;
import com.example.halil.user.domain.User;
import com.example.halil.user.domain.UserRepository;
import com.example.halil.user.domain.UserRole;
import com.example.halil.user.dto.UserCreationDto;
import com.example.halil.user.dto.UserSignupResponseDto;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원가입")
    void register_user() {
        // given
        long userId = 1L;
        String email = "thisisemail@email.com";
        String rawPassword = "this_is_password1234";
        String encodedPassword = "encodedPassword1234!!";
        UserCreationDto userCreationDto = new UserCreationDto(email, rawPassword);

        when(passwordService.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.findFirstByEmail(email)).thenReturn(Optional.empty());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        doAnswer(invocation -> {
            User parameter = invocation.getArgument(0);
            ReflectionTestUtils.setField(parameter, "id", userId);
            return null;
        }).when(userRepository)
                // ArgumentCaptor로 userRepository.save() 메소드에 들어오는 파라미터를 찍는다
                .save(userCaptor.capture());

        // when
        UserSignupResponseDto result = userService.create(userCreationDto);

        // then
        assertThat(result.userId()).isEqualTo(userCaptor.getValue().getId());
    }

    @Test
    @DisplayName("중복된 이메일은 회원가입 불가")
    void duplicate_email_cannot_register() {
        // given
        String email = "thisisemail@email.com";
        String rawPassword = "this_is_password!1";
        UserCreationDto userCreationDto = new UserCreationDto(email, rawPassword);
        when(userRepository.findFirstByEmail(email))
                .thenReturn(Optional.of(new User(email, null, null, null)));

        // when and then
        assertThatThrownBy(() -> userService.create(userCreationDto))
                .isInstanceOf(UserErrorCode.EMAIL_ALREADY_EXISTS.exception().getClass());
    }

    @Test
    @DisplayName("비밀번호 변경시 같은 비밀번호로 변경 불가")
    void password_cannot_be_reused() {
        // given
        long userId = 1L;
        String rawPassword = "raw_password1234";
        User user = new User("", "", UserRole.ROLE_USER, null);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(passwordService.matches(rawPassword, user.getEncodedPassword())).thenReturn(true);

        // when and then
        assertThatThrownBy(() -> userService.updatePassword(userId, rawPassword))
                .isInstanceOf(UserErrorCode.PASSWORD_CANNOT_BE_REUSED.exception().getClass());
    }
}
