package com.example.community.user.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignUpRequestDTO {
    @Email
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자로 설정해주세요.")
    private String password;

    @NotBlank(message = "비밀번호를 한번 더 입력해주세요.")
    private String passwordConfirm;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(max = 10, message = "닉네임은 최대 10자까지 작성 가능합니다.")
    private String nickname;

    private String profileImageUrl;

    public SignUpRequestDTO(String email, String password,  String passwordConfirm, String nickname, String profileImageUrl) {
        this.email = email;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}
