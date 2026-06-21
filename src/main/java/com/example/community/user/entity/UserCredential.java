package com.example.community.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="user_credentials",
        uniqueConstraints = {@UniqueConstraint(
                name = "uk_user_credentials_email",
                columnNames = "email"
        )}
)
@Getter
@NoArgsConstructor
public class UserCredential {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 20)
    private String password;

    public UserCredential(User user, String email, String password) {
        this.user = user;
        this.email = email;
        this.password = password;
    }
    public boolean matchPassword(String password) {
        return this.password.equals(password);
    }

    public void modifyPassword(String password) {
        this.password = password;
    }
}
