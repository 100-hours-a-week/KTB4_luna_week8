package com.example.community.global.auth;

import com.example.community.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "auth_tokens",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_auth_tokens_user", columnNames = "user_id"),
                @UniqueConstraint(name = "uk_auth_tokens_access_token", columnNames = "access_token"),
                @UniqueConstraint(name = "uk_auth_tokens_refresh_token", columnNames = "refresh_token")
        },
        indexes = {
                @Index(name = "idx_auth_tokens_expired_at", columnList = "access_token_expired_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long tokenId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "access_token", nullable = false)
    private String accessToken;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "access_token_expired_at", nullable = false)
    private LocalDateTime accessTokenExpiredAt;

    @Column(name = "refresh_token_expired_at", nullable = false)
    private LocalDateTime refreshTokenExpiredAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public AuthToken(User user, String accessToken, String refreshToken) {
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiredAt = LocalDateTime.now().plusHours(1);
        this.refreshTokenExpiredAt = LocalDateTime.now().plusHours(2);
        this.createdAt = LocalDateTime.now();
    }

    public boolean isAccessTokenExpired() {
        return LocalDateTime.now().isAfter(accessTokenExpiredAt);
    }
}