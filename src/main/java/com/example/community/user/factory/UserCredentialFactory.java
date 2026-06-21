package com.example.community.user.factory;

import com.example.community.user.dto.SignUpRequestDTO;
import com.example.community.user.entity.User;
import com.example.community.user.entity.UserCredential;
import org.springframework.stereotype.Component;

@Component
public class UserCredentialFactory {
    public UserCredential create(User user, SignUpRequestDTO signUpRequestDTO) {
        return new UserCredential(
                user,
                signUpRequestDTO.getEmail(),
                signUpRequestDTO.getPassword()
        );
    }
}
