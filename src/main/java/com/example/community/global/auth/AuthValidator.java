package com.example.community.global.auth;

import com.example.community.global.exceptions.ForbiddenException;
import com.example.community.global.exceptions.UnauthorizedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AuthValidator {
    private final TokenRepository tokenRepository;

    public AuthValidator(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public Long getLoginUserId(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank() || !authorizationHeader.startsWith("Bearer ")) throw new UnauthorizedException();

        String accessToken = authorizationHeader.substring("Bearer ".length());

        AuthToken token = tokenRepository.findByAccessToken(accessToken).orElseThrow(UnauthorizedException::new);

        if (token.isAccessTokenExpired()) {
            tokenRepository.delete(token);
            throw new UnauthorizedException();
        }
        return token.getUser().getUserId();
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
