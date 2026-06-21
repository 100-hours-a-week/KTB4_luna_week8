package com.example.community.post.repository;

import com.example.community.post.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report,Long> {
    @Query("""
            select case when count(r) > 0 then true else false end
            from Report r
            where r.post.postId = :postId and r.reporter.userId = :reporterId
            """)
    boolean existsByPostAndReporter(@Param("postId")long postId, @Param("reporterId") long reporterId);
    long countByPostPostId(long postId);
}
