package com.example.community.user.service;

import com.example.community.global.auth.AuthToken;
import com.example.community.global.auth.AuthTokenFactory;
import com.example.community.global.auth.AuthValidator;
import com.example.community.global.exceptions.*;
import com.example.community.user.dto.*;
import com.example.community.user.entity.User;
import com.example.community.user.entity.UserCredential;
import com.example.community.user.factory.UserCredentialFactory;
import com.example.community.user.factory.UserFactory;
import com.example.community.global.auth.TokenRepository;
import com.example.community.user.repository.UserCredentialRepository;
import com.example.community.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Service
@Validated
public class UserService {
    private final UserRepository userRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final TokenRepository tokenRepository;
    private final AuthValidator authValidator;
    private final UserFactory userFactory;
    private final UserCredentialFactory userCredentialFactory;
    private final AuthTokenFactory authTokenFactory;

    public UserService(UserRepository userRepository, UserCredentialRepository userCredentialRepository, TokenRepository tokenRepository, AuthValidator authValidator, UserFactory userFactory, UserCredentialFactory userCredentialFactory, AuthTokenFactory authTokenFactory) {
        this.userRepository = userRepository;
        this.userCredentialRepository = userCredentialRepository;
        this.tokenRepository = tokenRepository;
        this.authValidator = authValidator;
        this.userFactory = userFactory;
        this.userCredentialFactory = userCredentialFactory;
        this.authTokenFactory = authTokenFactory;
    }

    // ----------------------------------- 로그인, 토큰 생성 -----------------------------------
    @Transactional
    public LoginResponseDTO login(@Valid LoginRequestDTO requestDTO) {
        String email = requestDTO.getEmail();
        String password = requestDTO.getPassword();

        UserCredential credential = userCredentialRepository.findByEmail(email).orElseThrow(NotRegisteredException::new);
        User user = credential.getUser();

        if (!user.isActive()) throw new NotRegisteredException();
        if (!credential.matchPassword(password)) throw new PasswordInvalidException();

        String accessToken = "access-token" + user.getUserId();
        String refreshToken = "refresh-token" + user.getUserId();
        AuthToken authToken = authTokenFactory.create(user, accessToken, refreshToken);
        tokenRepository.save(authToken);

        return new LoginResponseDTO(user.getUserId(), accessToken, refreshToken, user.getNickname(), user.getProfileImageUrl());
    }
    // ----------------------------------- 로그아웃, 토큰 삭제 -----------------------------------
    @Transactional
    public void logout(String authorizationHeader){
        authValidator.getLoginUserId(authorizationHeader);
        String accessToken = authorizationHeader.substring("Bearer ".length());
        tokenRepository.deleteByAccessToken(accessToken);
    }
    // ----------------------------------- 회원가입(유저 생성) -----------------------------------
    @Transactional
    public SignUpResponseDTO signUp(@Valid SignUpRequestDTO requestDTO){
        if (!requestDTO.getPassword().equals(requestDTO.getPasswordConfirm())) throw new InvalidInputException();
        if(userCredentialRepository.existsByEmail(requestDTO.getEmail()) || userRepository.existsByNickname(requestDTO.getNickname())) throw new AlreadyExistsException();
        User user = userFactory.create(requestDTO);
        userRepository.save(user);
        UserCredential credential = userCredentialFactory.create(user, requestDTO);
        userCredentialRepository.save(credential);

        return new SignUpResponseDTO(user.getUserId());
    }

    // ----------------------------------- 유저 정보 수정(이름, 프로필사진) -----------------------------------
    @Transactional
    public ModifyInfoResponseDTO modifyInfo(Long userId, String authorizationHeader,@Valid ModifyInfoRequestDTO requestDTO){
        authValidator.validateOwner(authorizationHeader, userId);
        String nickname = requestDTO.getNickname();
        String profileImageUrl = requestDTO.getProfileImageUrl();
        if(userRepository.existsByNicknameAndUserIdNot(nickname, userId)) throw new AlreadyExistsException();

        User user =  userRepository.findById(userId).orElseThrow(NotRegisteredException::new);
        LocalDateTime updatedAt = LocalDateTime.now();

        user.modifyProfile(nickname, profileImageUrl);
        return new ModifyInfoResponseDTO(user.getUserId(), user.getNickname(), user.getProfileImageUrl(),  updatedAt);
    }
    // ----------------------------------- 유저 정보 수정(비밀번호) -----------------------------------
    @Transactional
    public void modifyPassword(Long userId, String authorizationHeader, @Valid ModifyPasswordRequestDTO requestDTO){
        authValidator.validateOwner(authorizationHeader, userId);

        UserCredential credential = userCredentialRepository.findById(userId).orElseThrow(NotRegisteredException::new);
        if (credential.matchPassword(requestDTO.getPassword())) throw new InvalidInputException();

        credential.modifyPassword(requestDTO.getPassword());
    }
    // ----------------------------------- 유저 탈퇴(유저 삭제) -----------------------------------
    @Transactional
    public WithdrawResponseDTO withdraw(Long userId, String authorizationHeader){
        authValidator.validateOwner(authorizationHeader, userId);
        User user = userRepository.findById(userId).orElseThrow(NotRegisteredException::new);
        user.withDraw();
        return new WithdrawResponseDTO(LocalDateTime.now());
    }
}
