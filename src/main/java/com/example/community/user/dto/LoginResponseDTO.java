package com.example.community.user.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String nickname;
    private String profileImageUrl;

    public LoginResponseDTO(String accessToken, String refreshToken, String nickname, String profileImageUrl) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}
