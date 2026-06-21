package com.example.community.post.repository;

import com.example.community.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    @Query("""
            select case when count(pl) > 0 then true else false end
            from PostLike pl
            where pl.user.userId = :userId and pl.post.postId = :postId
    """)
    boolean existsByUserAndPost(@Param("userId")Long userId, @Param("postId")Long postId);

    @Modifying
    @Query("""
        delete from PostLike pl
        where pl.user.userId = :userId and pl.post.postId = :postId
    """)
    void deletePostlike(@Param("userId") Long userId, @Param("postId") Long postId);
}
