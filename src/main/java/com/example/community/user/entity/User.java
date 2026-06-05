package com.example.community.user.entity;

import lombok.Getter;

@Getter
public class User {
    private Long userId;
    private String nickname;
    private String email;
    private String password;
    private String profileImageUrl;
    private UserStatus status;
    private UserRole role;

    public User(long id, String nickname, String email, String password, String profileImageUrl, UserRole userRole, UserStatus status) {
        this.userId = id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.profileImageUrl = profileImageUrl;
        this.status = status;
        this.role = userRole;
    }
    public boolean isActive(){
        return this.status.equals(UserStatus.ACTIVE);
    }
    public boolean matchPassword(String password){
        return this.password.equals(password);
    }
    public void modifyProfile(String nickname, String profileImageUrl){
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
    public void modifyPassword(String password){
        this.password = password;
    }
    public void withDraw(){
        this.status = UserStatus.WITHDRAWN;
        this.nickname = null;
        this.profileImageUrl = null;
        this.email = null;
        this.password = null;
    }
}
