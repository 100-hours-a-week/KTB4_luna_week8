package com.example.community.global.exceptions;

public class UnauthorizedException extends RuntimeException {
    // 토큰 만료 혹은 토큰이 현재 로그인 된 유저의 것이 아님.
}
