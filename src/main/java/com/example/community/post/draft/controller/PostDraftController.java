package com.example.community.post.draft.controller;

import com.example.community.global.ApiResponse;
import com.example.community.post.draft.dto.PostDraftRequestDTO;
import com.example.community.post.draft.dto.PostDraftResponseDTO;
import com.example.community.post.draft.service.PostDraftService;
import com.example.community.post.dto.PostResponseDTO;
import com.example.community.user.entity.UserRole;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/draft-post")
public class PostDraftController {
    private final PostDraftService postDraftService;
    public PostDraftController(PostDraftService postDraftService) {
        this.postDraftService = postDraftService;
    }
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<PostDraftResponseDTO>> getCurrentPost(Authentication authentication) {
        Long userId = getLoginUserId(authentication);
        Optional<PostDraftResponseDTO> responseDTO = postDraftService.getCurrentDraft(userId);

        return responseDTO.map(dto -> ResponseEntity.ok(new ApiResponse<>("draft_found", dto)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
    @PostMapping
    public ResponseEntity<ApiResponse<PostDraftResponseDTO>> saveDraft(Authentication authentication, @Valid @RequestBody PostDraftRequestDTO requestDTO){
        Long userId = getLoginUserId(authentication);

        PostDraftResponseDTO responseDTO = postDraftService.saveDraft(userId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("draft_post_success", responseDTO));
    }
    @PatchMapping()
    public ResponseEntity<ApiResponse<PostDraftResponseDTO>> updateDraft(Authentication authentication, @Valid @RequestBody PostDraftRequestDTO requestDTO){
        Long userId = getLoginUserId(authentication);

        PostDraftResponseDTO responseDTO = postDraftService.overwriteDraft(userId, requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("draft_overwrite_success", responseDTO));

    }
    @PostMapping("/publish")
    public ResponseEntity<ApiResponse<PostResponseDTO>> publishDraft(Authentication authentication, @Valid @RequestBody PostDraftRequestDTO requestDTO){
        Long userId = getLoginUserId(authentication);

        PostResponseDTO responseDTO = postDraftService.publishDraft(userId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("draft_published", responseDTO));
    }
    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteDraft(Authentication authentication){
        Long userId = getLoginUserId(authentication);

        postDraftService.deleteDraft(userId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("draft_delete_success", null));
    }
    private Long getLoginUserId(Authentication authentication){
        return Long.valueOf(authentication.getName());
    }

    private UserRole getLoginUserRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority ->
                        authority.equals(UserRole.ROLE_ADMIN.name())
                                || authority.equals(UserRole.ROLE_USER.name())
                )
                .map(UserRole::valueOf)
                .findFirst()
                .orElse(UserRole.ROLE_USER);
    }
}
