package com.example.community.user.factory;

import com.example.community.user.dto.UserDTO;
import com.example.community.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    public User create(UserDTO userDTO) {
        return new User(
                userDTO.getUserId(),
                userDTO.getNickname(),
                userDTO.getEmail(),
                userDTO.getPassword(),
                userDTO.getProfileImageUrl(),
                userDTO.getRole(),
                userDTO.getStatus()
        );
    }
}
