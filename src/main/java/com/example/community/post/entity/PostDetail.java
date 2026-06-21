package com.example.community.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name="post_details")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostDetail {
    @Id
    @Column(name = "post_id")
    private Long postId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name="post_body", nullable = false, columnDefinition = "TEXT")
    private String postBody;

    @Column(name = "post_image_url", columnDefinition = "TEXT")
    private String postImageUrl;

    public PostDetail(Post post, String postBody, String postImageUrl) {
        this.post = post;
        this.postBody = postBody;
        this.postImageUrl = postImageUrl;
    }

    public void modify(String postBody, String postImageUrl) {
        this.postBody = postBody;
        this.postImageUrl = postImageUrl;
    }
}
