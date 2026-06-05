package com.example.community.post.dto;

import com.example.community.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostItemDTO {
    private Long postId;
    private String title;
    private LocalDateTime createdAt;
    private int likes;
    private int comments;
    private int views;
    public PostItemDTO(Post post){
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.createdAt = post.getCreatedAt();
        this.likes = post.getLikes();
        this.comments = post.getComments();
        this.views = post.getViews();
    }
}
