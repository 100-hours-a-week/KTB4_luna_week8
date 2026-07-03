package com.example.community.user.service;

import com.example.community.global.auth.JwtToken;
import com.example.community.global.auth.JwtTokenProvider;
import com.example.community.user.dto.LoginRequestDTO;
import com.example.community.user.dto.LoginResponseDTO;
import com.example.community.user.entity.User;
import com.example.community.user.entity.UserCredential;
import com.example.community.user.entity.UserRole;
import com.example.community.user.entity.UserStatus;
import com.example.community.user.repository.UserCredentialRepository;
import com.example.community.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserCredentialRepository userCredentialRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("로그인 한 유저에게 토큰이 발급 된다.")
    void login_returnsToken(){
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("test1@email.com");
        request.setPassword("Test1234!");
        User user = new User(1L, "test", "", UserRole.ROLE_USER, UserStatus.ACTIVE);
        UserCredential credential = new UserCredential(user, request.getEmail(), request.getPassword());

        JwtToken jwtToken = JwtToken.builder()
                .grantType("Bearer")
                .accessToken("jwt-access-token")
                .refreshToken("jwt-refresh-token")
                .build();

        when(userCredentialRepository.findByEmail("test1@email.com")).thenReturn(Optional.of(credential));
        when(jwtTokenProvider.createJwtToken(user)).thenReturn(jwtToken);

        LoginResponseDTO response = userService.login(request);

        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getToken()).isEqualTo(jwtToken);
        assertThat(response.getNickname()).isEqualTo("test");

        verify(jwtTokenProvider).createJwtToken(user);
    }
}
