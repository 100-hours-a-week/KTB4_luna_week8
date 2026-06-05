package com.example.community.post.entity;

import com.example.community.post.dto.PostRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private Long postId;
    private Long userId;

    private String title;
    private String postBody;
    private String postImageUrl;

    private LocalDateTime createdAt;

    private boolean modified;
    private LocalDateTime modifiedAt;
    private int revision;

    private PostStatus status;

    private int likes;
    private int views;
    private int comments;

    public void modifyPost(String title, String postBody, String postImageUrl){
        this.title = title;
        this.postBody = postBody;
        this.postImageUrl = postImageUrl;
        this.modified = true;
        this.modifiedAt = LocalDateTime.now();
        this.revision++;
    }
    public void increaseComments(){
        this.comments++;
    }

    public void increaseViews(){
        this.views++;
    }
    public void increaseLikes(){
        this.likes++;
    }
    public void decreaseLikes(){
        this.likes--;
    }
    public void deletePost(){
        this.title = null;
        this.postBody = null;
        this.postImageUrl = null;
        this.status = PostStatus.DELETED;
    }
}
