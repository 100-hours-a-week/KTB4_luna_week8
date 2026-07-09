package com.example.community.user.factory;

import com.example.community.user.dto.SignUpRequestDTO;
import com.example.community.user.entity.User;
import com.example.community.user.entity.UserCredential;
import com.example.community.user.entity.UserRole;
import com.example.community.user.entity.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
class UserCredentialFactoryTest {

    PasswordEncoder passwordEncoder;
    UserCredentialFactory userCredentialFactory;

    User user;
    SignUpRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userCredentialFactory = new UserCredentialFactory(passwordEncoder);

        user = new User(1L, "tester", "", UserRole.ROLE_USER, UserStatus.ACTIVE);

        requestDTO = new SignUpRequestDTO(
                "test@test.com",
                "Test1234!",
                "Test1234!",
                "tester",
                ""
        );
    }

    @Test
    @DisplayName("회원가입 비밀번호는 BCrypt로 암호화되어 저장된다.")
    void create_encryptsPassword() {
        UserCredential credential = userCredentialFactory.create(user, requestDTO);

        assertThat(credential.getEmail()).isEqualTo("test@test.com");
        assertThat(credential.getPassword()).isNotEqualTo("Test1234!");
        assertThat(passwordEncoder.matches("Test1234!", credential.getPassword())).isTrue();
    }
}