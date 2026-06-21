package com.example.community.post.draft.entity;

import com.example.community.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Entity
@Table(name = "drafts", uniqueConstraints = {@UniqueConstraint(name = "uk_drafts_author", columnNames = "author_id")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostDraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "draft_id")
    private long draftId;

    @OneToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 26)
    private String title;
    @Column(name = "post_body", nullable = false, columnDefinition = "TEXT")
    private String postBody;
    @Column(name = "post_image_url", columnDefinition = "TEXT")
    private String postImageUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private int version;

    public PostDraft(User author, String title, String postBody, String postImageUrl) {
        this.author = author;
        this.title = title;
        this.postBody = postBody;
        this.postImageUrl = postImageUrl;
        this.createdAt = LocalDateTime.now();
        this.version = 1;
    }
    public void overwrite(String title, String postBody, String postImageUrl) {
        this.title = title;
        this.postBody = postBody;
        this.postImageUrl = postImageUrl;
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
}
