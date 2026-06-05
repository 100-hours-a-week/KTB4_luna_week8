package com.example.community.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class LoginRequestDTO {
    @NotBlank(message="email을 입력해주세요.")
    @Email(message="email이 올바르지 않습니다.")
    private String email;

    @NotBlank(message="비밀번호를 입력해주세요.")
    private String password;
}
