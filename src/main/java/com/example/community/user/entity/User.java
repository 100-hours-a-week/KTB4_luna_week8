package com.example.community.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_nickname",
                        columnNames = "nickname"
                )
        }
)
@Getter
@NoArgsConstructor
@Check(name = "chk_users_nickname_not_blank", constraints = "LENGTH(TRIM(nickname)) > 0")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, length = 10)
    private String nickname;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole role;

    public User(long id, String nickname, String profileImageUrl, UserRole userRole, UserStatus status) {
        this.userId = id;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.status = status;
        this.role = userRole;
    }

    public User(@NotBlank(message = "닉네임을 입력해주세요.") @Size(max = 10, message = "닉네임은 최대 10자까지 작성 가능합니다.") String nickname, String profileImageUrl, UserRole userRole, UserStatus userStatus) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.role = userRole;
        this.status = userStatus;
    }

    public boolean isActive(){
        return this.status.equals(UserStatus.ACTIVE);
    }
    public void modifyProfile(String nickname, String profileImageUrl){
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
    public void withDraw(){
        this.status = UserStatus.WITHDRAWN;
        this.profileImageUrl = null;
    }
}
