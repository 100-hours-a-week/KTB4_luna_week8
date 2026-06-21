package com.example.community.global.auth;

import com.example.community.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AuthTokenFactory {
    public AuthToken create(User user, String accessToken, String refreshToken){
        return new AuthToken(user, accessToken, refreshToken);
    }
}
