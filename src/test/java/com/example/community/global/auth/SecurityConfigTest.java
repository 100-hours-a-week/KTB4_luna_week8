package com.example.community.global.auth;

import com.example.community.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    UserService userService;
    @Test
    @DisplayName("로그인 페이지는 인증 없이도 로그인이 가능하다.")
    void loginRequest_canBeAccessedWithoutLogin() throws Exception{
        mockMvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON).content("""
                            {
                              "email": "test@example.com",
                              "password": "password123!"
                            }
                            """))
                .andExpect(status().isOk());
    }

    @Test
    void signupRequest_canBeAccessedWithoutLogin() throws Exception{
        mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content("""
                            {
                              "email": "test@example.com",
                              "password": "password123!"
                            }
                            """))
                .andExpect(status().isOk());
    }

    @Test
    void otherPages_redirectToLogin_whenUserIsNotLoggedIn() throws Exception{
        mockMvc.perform(get("/api/posts")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser
    void otherPages_canBeAccessed_whenUserIsLoggedIn() throws Exception{
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk());
    }
}
