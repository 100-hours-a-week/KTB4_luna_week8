package com.example.community.post.entity;

import com.example.community.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Entity
@Table(name="post_reports",
        uniqueConstraints = {
            @UniqueConstraint(
                name = "uk_reports_post_reporter",
                columnNames = {"post_id", "reporter_id"}
        )
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_reason", nullable = false)
    private ReportReason reason;

    @Column(length = 100)
    private String description;

    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt;

    public Report(Post post, User reporter, ReportReason reason, String description) {
        this.post = post;
        this.reporter = reporter;
        this.reason = reason;
        this.description = description;
        this.reportedAt = LocalDateTime.now();
    }

    public Long getPostId() {
        return post.getPostId();
    }

    public Long getReporterId() {
        return reporter.getUserId();
    }
}
