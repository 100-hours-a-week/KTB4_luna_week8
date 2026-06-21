package com.example.community.post.draft.factory;

import com.example.community.post.draft.dto.PostDraftRequestDTO;
import com.example.community.post.draft.entity.PostDraft;
import com.example.community.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PostDraftFactory {
    public PostDraft create(User author, PostDraftRequestDTO requestDTO){
        return new PostDraft(
                author,
                requestDTO.getTitle(),
                requestDTO.getPostBody(),
                requestDTO.getPostImageUrl()
        );
    }
}
