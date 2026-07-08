package com.example.community.user.integration;

import com.example.community.user.entity.User;
import com.example.community.user.entity.UserCredential;
import com.example.community.user.entity.UserRole;
import com.example.community.user.entity.UserStatus;
import com.example.community.user.repository.UserCredentialRepository;
import com.example.community.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserCredentialRepository userCredentialRepository;

    User user;
    UserCredential credential;

    @BeforeEach
    void setUp() {
        userCredentialRepository.deleteAll();
        userRepository.deleteAll();

        user = new User("tester", "", UserRole.ROLE_USER, UserStatus.ACTIVE);
        userRepository.save(user);

        credential = new UserCredential(user, "test1@test.com", "Test1234!");
        userCredentialRepository.save(credential);
    }

    @Test
    @DisplayName("로그인 요청이 Controller-Service-Repository를 거쳐 JWT를 반환한다.")
    void login_success() throws Exception {
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "email": "test1@test.com",
                          "password": "Test1234!"
                        }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("user_login_success"))
                .andExpect(jsonPath("$.data.userId").value(user.getUserId()))
                .andExpect(jsonPath("$.data.token.grantType").value("Bearer"))
                .andExpect(jsonPath("$.data.token.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.token.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.data.nickname").value("tester"));
    }

    @Test
    @DisplayName("회원가입 요청이 실제 DB에 User와 UserCredential을 저장한다.")
    void signUp_success() throws Exception {
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "email": "new1@test.com",
                          "password": "Test1234!",
                          "passwordConfirm": "Test1234!",
                          "nickname": "new1",
                          "profileImageUrl": ""
                        }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("user_register_success"))
                .andExpect(jsonPath("$.data.userId").exists());

        assertThat(userCredentialRepository.existsByEmail("new1@test.com")).isTrue();
        assertThat(userRepository.existsByNickname("new1")).isTrue();
    }

    @Test
    @DisplayName("JWT 인증 후 회원정보 수정 요청이 실제 DB에 반영된다.")
    void modifyInfo_success() throws Exception {
        String accessToken = loginAndGetAccessToken();

        mockMvc.perform(patch("/api/users/" + user.getUserId() + "/info")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "nickname": "updated",
                          "profileImageUrl": "profile.png"
                        }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("user_modify_success"))
                .andExpect(jsonPath("$.data.nickname").value("updated"));

        User updatedUser = userRepository.findById(user.getUserId()).orElseThrow();

        assertThat(updatedUser.getNickname()).isEqualTo("updated");
        assertThat(updatedUser.getProfileImageUrl()).isEqualTo("profile.png");
    }

    @Test
    @DisplayName("JWT 인증 후 비밀번호 변경 요청이 실제 DB에 반영된다.")
    void modifyPassword_success() throws Exception {
        String accessToken = loginAndGetAccessToken();

        mockMvc.perform(patch("/api/users/" + user.getUserId() + "/password")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "password": "New12345!",
                          "passwordConfirm": "New12345!"
                        }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("password_modify_success"));

        UserCredential updatedCredential = userCredentialRepository.findById(user.getUserId()).orElseThrow();

        assertThat(updatedCredential.matchPassword("New12345!")).isTrue();
    }

    @Test
    @DisplayName("JWT 인증 후 회원 탈퇴 요청이 실제 DB에 WITHDRAWN 상태로 반영된다.")
    void withdraw_success() throws Exception {
        String accessToken = loginAndGetAccessToken();

        mockMvc.perform(delete("/api/users/" + user.getUserId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("user_withdraw_success"));

        User withdrawnUser = userRepository.findById(user.getUserId()).orElseThrow();

        assertThat(withdrawnUser.getStatus()).isEqualTo(UserStatus.WITHDRAWN);
        assertThat(withdrawnUser.getProfileImageUrl()).isNull();
    }

    private String loginAndGetAccessToken() throws Exception {
        String response = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "email": "test1@test.com",
                          "password": "Test1234!"
                        }
                    """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = new ObjectMapper().readTree(response);

        return json.get("data").get("token").get("accessToken").asText();
    }
}