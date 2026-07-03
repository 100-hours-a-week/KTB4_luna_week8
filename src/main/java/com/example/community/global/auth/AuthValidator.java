package com.example.community.global.auth;

import com.example.community.global.exceptions.ForbiddenException;
import com.example.community.global.exceptions.UnauthorizedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AuthValidator {

    public AuthValidator() {
    }

    @Transactional
    public Long getLoginUserId(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank() || !authorizationHeader.startsWith("Bearer ")) throw new UnauthorizedException();

        String accessToken = authorizationHeader.substring("Bearer ".length());

        return 1L;
    }

    @Transactional
    public void validateOwner(String authorizationHeader, Long ownerId) {
        Long loginUserId = getLoginUserId(authorizationHeader);

        if (!loginUserId.equals(ownerId)) throw new ForbiddenException();

    }
    public void validateOwner(Long ownerId, Long authorId) {
        if (!ownerId.equals(authorId)) throw new ForbiddenException();
    }
}
