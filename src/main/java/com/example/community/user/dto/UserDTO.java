package com.example.community.user.dto;

import com.example.community.user.entity.UserRole;
import com.example.community.user.entity.UserStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {
    private Long userId;
    private String nickname;
    private String email;
    private String password;
    private String profileImageUrl;
    private UserRole role;
    private UserStatus status;

}
