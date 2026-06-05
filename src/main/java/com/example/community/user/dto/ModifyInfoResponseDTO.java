package com.example.community.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ModifyInfoResponseDTO {
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private LocalDateTime updatedAt;

    public ModifyInfoResponseDTO(Long userId, String nickname, String profileImageUrl, LocalDateTime updatedAt) {
        this.userId = userId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.updatedAt = updatedAt;
    }
}
