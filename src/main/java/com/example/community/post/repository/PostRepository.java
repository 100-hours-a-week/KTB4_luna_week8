package com.example.community.post.repository;

import com.example.community.post.entity.Post;
import com.example.community.post.entity.PostStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    @Query("""
        select p 
        from Post p
        join fetch p.author
        where p.status <> :status
        order by p.createdAt desc
    """)
    List<Post> findByStatusNot(@Param("status") PostStatus status);

    @EntityGraph(attributePaths = {"author", "detail"})
    Optional<Post> findByPostId(Long postId);
}
