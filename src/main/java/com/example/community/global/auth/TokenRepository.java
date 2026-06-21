package com.example.community.global.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<AuthToken, Long> {

    Optional<AuthToken> findByAccessToken(String accessToken);

    void deleteByAccessToken(String accessToken);
}