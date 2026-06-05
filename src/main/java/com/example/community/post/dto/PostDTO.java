package com.example.community.post.dto;

import com.example.community.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDTO {
    private long postId;
    private String title;
    private String postBody;
    private String postImageUrl;
    private LocalDateTime createdAt;
    private boolean modified;
    private LocalDateTime modifiedAt;
    private int revision;
    public PostDTO(Post post) {
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.postBody = post.getPostBody();
        this.postImageUrl = post.getPostImageUrl();
        this.createdAt = post.getCreatedAt();
        this.modified = post.isModified();
        this.modifiedAt = post.getModifiedAt();
        this.revision = post.getRevision();
    }
}
