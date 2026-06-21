package com.example.community.comment.entity;

import com.example.community.post.entity.Post;
import com.example.community.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@Table(name = "comments",
        indexes = {
                @Index(name = "idx_comments_post_created_at", columnList = "post_id, created_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @Column(name = "comment_body", nullable = false, columnDefinition = "TEXT")
    private String commentBody;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean modified;
    @Column(name="modified_at")
    private LocalDateTime modifiedAt;

    @Column(nullable = false)
    private boolean deleted;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Comment(User author, Post post, Comment parentComment, String commentBody) {
        this.author = author;
        this.post = post;
        this.parentComment = parentComment;
        this.commentBody = commentBody;
        this.createdAt = LocalDateTime.now();
        this.modified = false;
        this.deleted = false;
    }

    public void modify(String commentBody) {
        this.commentBody = commentBody;
        modified = true;
        modifiedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        this.commentBody = "삭제된 댓글입니다";
    }
}
