package com.example.community.user.factory;

import com.example.community.user.dto.SignUpRequestDTO;
import com.example.community.user.entity.User;
import com.example.community.user.entity.UserRole;
import com.example.community.user.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {
    public User create(SignUpRequestDTO requestDTO) {
        return new User(
                requestDTO.getNickname(),
                requestDTO.getProfileImageUrl(),
                UserRole.USER,
                UserStatus.ACTIVE
        );
    }
}
