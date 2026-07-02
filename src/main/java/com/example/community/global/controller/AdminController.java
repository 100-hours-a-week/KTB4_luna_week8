package com.example.community.global.controller;

import com.example.community.comment.dto.CommentRemoveResponseDTO;
import com.example.community.global.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @GetMapping
    public ResponseEntity<String> adminPage() {
        return ResponseEntity.status(HttpStatus.OK).body("Test");
    }
}
