package com.example.community.global.auth;

import com.example.community.post.controller.PostController;
import com.example.community.post.service.PostService;
import com.example.community.user.controller.UserController;
import com.example.community.user.dto.LoginResponseDTO;
import com.example.community.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class, PostController.class})
@Import(SecurityConfig.class)
public class SecurityConfigTest {
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    UserService userService;
    @MockitoBean
    PostService postService;

    @Test
    @DisplayName("로그인 페이지는 인증 없이도 로그인이 가능하다.")
    void loginRequest_canBeAccessedWithoutLogin() throws Exception{
        when(userService.login(any())).thenReturn(new LoginResponseDTO("access-token", "refresh-token", "nickname", ""));

        mockMvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON).content("""
                    {
                        "email":"test@test.com",
                        "password":"Test1234!
                    }
                """)).andExpect(status().isOk());
    }

//    @Test
//    void signupRequest_canBeAccessedWithoutLogin() throws Exception{
//        mockMvc.perform(post("/api/users/signup").contentType(MediaType.APPLICATION_JSON).content("""
//                            {
//                              "email": "test@example.com",
//                              "password": "password123!"
//                            }
//                            """))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void otherPages_redirectToLogin_whenUserIsNotLoggedIn() throws Exception{
//        mockMvc.perform(get("/api/posts")).andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrlPattern("**/login"));
//    }
//
//    @Test
//    @WithMockUser
//    void otherPages_canBeAccessed_whenUserIsLoggedIn() throws Exception{
//        mockMvc.perform(get("/api/posts"))
//                .andExpect(status().isOk());
//    }
}
