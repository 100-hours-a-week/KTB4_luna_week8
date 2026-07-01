package com.example.community.user.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private long userId;
    private String accessToken;
    private String refreshToken;
    private String nickname;
    private String profileImageUrl;

    public LoginResponseDTO(long userId, String accessToken, String refreshToken, String nickname, String profileImageUrl) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}
