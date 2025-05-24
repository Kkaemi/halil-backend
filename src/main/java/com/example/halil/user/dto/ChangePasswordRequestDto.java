package com.example.halil.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ChangePasswordRequestDto {

    @Pattern(regexp = "^(?=\\S{8,})(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]:;,.?]).*$")
    @NotBlank
    private String password;
}
