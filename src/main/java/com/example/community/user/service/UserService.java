package com.example.community.user.service;

import com.example.community.global.auth.AuthValidator;
import com.example.community.global.exceptions.*;
import com.example.community.user.dto.*;
import com.example.community.user.entity.User;
import com.example.community.user.entity.UserRole;
import com.example.community.user.entity.UserStatus;
import com.example.community.user.repository.TokenRepository;
import com.example.community.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDateTime;

@Service
@Validated
public class UserService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final AuthValidator authValidator;

    public UserService(UserRepository userRepository,  TokenRepository tokenRepository,  AuthValidator authValidator) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.authValidator = authValidator;
    }

    // ----------------------------------- 로그인, 토큰 생성 -----------------------------------
    public LoginResponseDTO login(@Valid LoginRequestDTO requestDTO) {
        String email = requestDTO.getEmail();
        String password = requestDTO.getPassword();
        User user = userRepository.findUserByEmail(email).orElseThrow(NotRegisteredException::new);
        if (!user.isActive()) throw new NotRegisteredException();
        if (!user.matchPassword(password)) throw new PasswordInvalidException();

        String accessToken = "access-token" + user.getUserId();
        String refreshToken = "refresh-token" + user.getUserId();
        tokenRepository.save(accessToken, user.getUserId(), LocalDateTime.now().plusHours(1));
        return new LoginResponseDTO(accessToken, refreshToken, user.getNickname(), user.getProfileImageUrl());
    }
    // ----------------------------------- 로그아웃, 토큰 삭제 -----------------------------------
    public void logout(String authorizationHeader){
        authValidator.getLoginUserId(authorizationHeader);
        String accessToken = authorizationHeader.substring("Bearer ".length());
        tokenRepository.delete(accessToken);
    }
    // ----------------------------------- 회원가입(유저 생성) -----------------------------------
    public SignUpResponseDTO signUp(@Valid SignUpRequestDTO requestDTO){
        String email = requestDTO.getEmail();
        String password = requestDTO.getPassword();
        String passwordConfirm = requestDTO.getPasswordConfirm();
        String nickname = requestDTO.getNickname();
        String profileImageUrl = requestDTO.getProfileImageUrl();

        if(!password.equals(passwordConfirm)) throw new InvalidInputException() ;
        if(userRepository.findUserByEmail(email).isPresent() || userRepository.findUserByNickname(nickname).isPresent()) throw new AlreadyExistsException();
        Long userId = userRepository.nextUserId();
        User user = new User(userId, nickname, email, password, profileImageUrl, UserRole.USER, UserStatus.ACTIVE);
        userRepository.save(user);
        return new SignUpResponseDTO(user.getUserId());
    }

    // ----------------------------------- 유저 정보 수정(이름, 프로필사진) -----------------------------------
    public ModifyInfoResponseDTO modifyInfo(Long userId, String authorizationHeader,@Valid ModifyInfoRequestDTO requestDTO){
        authValidator.validateOwner(authorizationHeader, userId);
        String nickname = requestDTO.getNickname();
        String profileImageUrl = requestDTO.getProfileImageUrl();
        if(userRepository.existsByNicknameExceptUserId(nickname, userId)) throw new AlreadyExistsException();

        User user =  userRepository.findUserById(userId).orElseThrow(NotRegisteredException::new);
        LocalDateTime updatedAt = LocalDateTime.now();

        user.modifyProfile(nickname, profileImageUrl);
        return new ModifyInfoResponseDTO(user.getUserId(), user.getNickname(), user.getProfileImageUrl(),  updatedAt);
    }
    // ----------------------------------- 유저 정보 수정(비밀번호) -----------------------------------
    public void modifyPassword(Long userId, String authorizationHeader, @Valid ModifyPasswordRequestDTO requestDTO){
        authValidator.validateOwner(authorizationHeader, userId);

        User user = userRepository.findUserById(userId).orElseThrow(NotRegisteredException::new);
        if(user.matchPassword(requestDTO.getPassword())) throw new InvalidInputException();
        user.modifyPassword(requestDTO.getPassword());
    }
    // ----------------------------------- 유저 탈퇴(유저 삭제) -----------------------------------
    public WithdrawResponseDTO withdraw(Long userId, @RequestHeader(value = "Authorization") String authorizationHeader){
        authValidator.validateOwner(authorizationHeader, userId);
        User user = userRepository.findUserById(userId).orElseThrow(NotRegisteredException::new);
        user.withDraw();
        return new WithdrawResponseDTO(LocalDateTime.now());
    }
}
