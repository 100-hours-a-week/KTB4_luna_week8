package com.example.community.user.factory;

import com.example.community.user.dto.SignUpRequestDTO;
import com.example.community.user.entity.User;
import com.example.community.user.entity.UserCredential;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserCredentialFactory {
    private PasswordEncoder passwordEncoder;
    public UserCredentialFactory(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }
    public UserCredential create(User user, SignUpRequestDTO signUpRequestDTO) {
        return new UserCredential(
                user,
                signUpRequestDTO.getEmail(),
                passwordEncoder.encode(signUpRequestDTO.getPassword())
        );
    }
}
