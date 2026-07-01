package com.example.community.post.draft.controller;

import com.example.community.global.ApiResponse;
import com.example.community.post.draft.dto.PostDraftRequestDTO;
import com.example.community.post.draft.dto.PostDraftResponseDTO;
import com.example.community.post.draft.service.PostDraftService;
import com.example.community.post.dto.PostResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<PostDraftResponseDTO>> getCurrentPost(@RequestHeader(value = "Authorization") String authorizationHeader){
        Optional<PostDraftResponseDTO> responseDTO = postDraftService.getCurrentDraft(authorizationHeader);

        return responseDTO.map(dto -> ResponseEntity.ok(new ApiResponse<>("draft_found", dto)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
    @PostMapping
    public ResponseEntity<ApiResponse<PostDraftResponseDTO>> saveDraft(@RequestHeader(value = "Authorization") String authorizationHeader, @Valid @RequestBody PostDraftRequestDTO requestDTO){
        PostDraftResponseDTO responseDTO = postDraftService.saveDraft(authorizationHeader, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("draft_post_success", responseDTO));
    }
    @PatchMapping()
    public ResponseEntity<ApiResponse<PostDraftResponseDTO>> updateDraft(@RequestHeader(value = "Authorization") String authorizationHeader, @Valid @RequestBody PostDraftRequestDTO requestDTO){
        PostDraftResponseDTO responseDTO = postDraftService.overwriteDraft(authorizationHeader, requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("draft_overwrite_success", responseDTO));

    }
    @PostMapping("/publish")
    public ResponseEntity<ApiResponse<PostResponseDTO>> publishDraft(@RequestHeader(value = "Authorization") String authorizationHeader, @Valid @RequestBody PostDraftRequestDTO requestDTO){
        PostResponseDTO responseDTO = postDraftService.publishDraft(authorizationHeader, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("draft_published", responseDTO));
    }
    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteDraft(@RequestHeader(value = "Authorization") String authorizationHeader){
        postDraftService.deleteDraft(authorizationHeader);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("draft_delete_success", null));
    }
}
