package com.example.halil.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserCreationDto {

    @NotBlank
    @Email
    private String email;

    // 길이 8자 이상
    // 숫자 하나 이상 포함
    // 특수문자 하나 이상 포함 (!@#$%^&*()_+-={}[]:;,.?)
    // 앞뒤 공백 없음
    @NotBlank
    @Pattern(regexp = "^(?=\\S{8,})(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]:;,.?]).*$")
    private String password;
}
