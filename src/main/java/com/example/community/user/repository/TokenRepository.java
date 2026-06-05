package com.example.community.user.repository;

import lombok.Getter;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class TokenRepository {
    private final Map<String, TokenInfo> accessTokens = new HashMap<>();
    public static class TokenInfo{
        @Getter
        private final Long userId;
        private final LocalDateTime expiredAt;
        public TokenInfo(Long userId, LocalDateTime expiredAt) {
            this.userId = userId;
            this.expiredAt = expiredAt;
        }
        public boolean checkExpired() {
            return LocalDateTime.now().isAfter(expiredAt);
        }
    }
    public void save(String accessToken, Long userId, LocalDateTime expiredAt) {
        accessTokens.put(accessToken, new TokenInfo(userId, expiredAt));
    }
    public Optional<TokenInfo> findByAccessToken(String accessToken) {
        return Optional.ofNullable(accessTokens.get(accessToken));
    }
    public void delete(String accessToken) {
        accessTokens.remove(accessToken);
    }
}
