package com.example.community.post.draft.repository;

import com.example.community.post.draft.entity.PostDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostDraftRepository  extends JpaRepository<PostDraft, Long> {
    Optional<PostDraft> findByAuthorUserId(Long userId);
}
