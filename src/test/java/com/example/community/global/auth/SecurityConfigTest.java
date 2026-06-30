package com.example.community.global.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {
    @Autowired
    MockMvc mockMvc;
    @Test
    void loginPage_canBeAccessedWithoutLogin() throws Exception{
        mockMvc.perform(get("/login")).andExpect(status().isOk());
    }
    @Test
    void otherPages_redirectToLogin_whenUserIsNotLoggedIn() throws Exception{
        mockMvc.perform(get("/posts")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser
    void otherPages_canBeAccessed_whenUserIsLoggedIn() throws Exception{
        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk());
    }
}
