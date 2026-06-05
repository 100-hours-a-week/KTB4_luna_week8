package com.example.community.user.dto;

import com.example.community.user.entity.UserStatus;
import lombok.Data;

import java.util.Date;

@Data
public class SignUpResponseDTO {
    private long userId;
    private UserStatus userStatus;
    private Date createdAt;

    public SignUpResponseDTO(Long userId) {
        this.userId = userId;
        this.userStatus = UserStatus.ACTIVE;
        this.createdAt = new Date();
    }
}
