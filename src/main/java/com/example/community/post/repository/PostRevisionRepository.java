package com.example.community.post.repository;

import com.example.community.post.entity.PostRevision;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRevisionRepository extends JpaRepository<PostRevision, Long> {
}
