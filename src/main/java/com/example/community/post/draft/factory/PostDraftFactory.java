package com.example.community.post.draft.factory;

import com.example.community.post.draft.dto.PostDraftRequestDTO;
import com.example.community.post.draft.entity.PostDraft;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PostDraftFactory {
    public PostDraft create(long draftId, long userId, PostDraftRequestDTO requestDTO){
        return new PostDraft(
                draftId,
                userId,
                requestDTO.getTitle(),
                requestDTO.getPostBody(),
                requestDTO.getPostImageUrl(),
                LocalDateTime.now(),
                null,
                1
        );
    }
}
