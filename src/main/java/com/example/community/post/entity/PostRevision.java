package com.example.community.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name="post_revisions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_post_revisions_post_revision",
                        columnNames = {"post_id", "revision"}
                )
        }
)

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostRevision {
    @Id
    @Column(name = "revision_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long revisionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false, length = 26)
    private String title;

    @Column(name = "post_body", nullable = false, columnDefinition = "TEXT")
    private String postBody;

    @Column(name = "post_image_url", columnDefinition = "TEXT")
    private String postImageUrl;

    @Column(nullable = false)
    private int revision;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public PostRevision(Post post) {
        this.post = post;
        this.title = post.getTitle();
        this.postBody = post.getDetail().getPostBody();
        this.postImageUrl = post.getDetail().getPostImageUrl();
        this.revision = post.getRevision();
        this.createdAt = LocalDateTime.now();
    }
}
