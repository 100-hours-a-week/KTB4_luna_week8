package com.example.community.user.factory;

import com.example.community.user.dto.SignUpRequestDTO;
import com.example.community.user.entity.User;
import com.example.community.user.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserFactoryTest {
    private UserFactory userFactory;
    @BeforeEach
    void setUp(){
        userFactory = new UserFactory();
    }
    @Test
    @DisplayName("관리자 생성 확인")
    void testCreateAdmin(){
        User admin = userFactory.create("admin", "", UserRole.ROLE_ADMIN);
        assertThat(admin.getNickname()).isEqualTo("admin");
        assertThat(admin.getRole()).isEqualTo(UserRole.ROLE_ADMIN);
    }
    @Test
    @DisplayName("일반 유저 생성")
    void testCreateUser(){
        User user = userFactory.create(new SignUpRequestDTO("test1@email.com", "Test1234!", "Test1234!", "user", ""));
        assertThat(user.getNickname()).isEqualTo("user");
        assertThat(user.getRole()).isEqualTo(UserRole.ROLE_USER);
    }
}
