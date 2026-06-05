package com.example.community.global.auth;

import com.example.community.global.exceptions.ForbiddenException;
import com.example.community.global.exceptions.UnauthorizedException;
import com.example.community.user.repository.TokenRepository;
import org.springframework.stereotype.Component;

@Component
public class AuthValidator {
    private final TokenRepository tokenRepository;
    public AuthValidator(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }
    public Long getLoginUserId(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank() ||!authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException();
        }

        String accessToken = authorizationHeader.substring("Bearer ".length());

        TokenRepository.TokenInfo tokenInfo = tokenRepository.findByAccessToken(accessToken).orElseThrow(UnauthorizedException::new);

        if (tokenInfo.checkExpired()) {
            tokenRepository.delete(accessToken);
            throw new UnauthorizedException();
        }
        return tokenInfo.getUserId();
    }

    public void validateOwner(String authorizationHeader, Long ownerId) {
        Long loginUserId = getLoginUserId(authorizationHeader);
        if (!loginUserId.equals(ownerId)) throw new ForbiddenException();
    }

    public void validateOwner(Long ownerId, Long authorId){
        if(!ownerId.equals(authorId)) throw new ForbiddenException();
    }
}
