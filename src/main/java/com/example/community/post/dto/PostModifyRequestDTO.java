package com.example.community.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostModifyRequestDTO {
    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 26, message = "제목은 최대 26자입니다.")
    private String title;

    @NotBlank(message = "본문을 입력해주세요.")
    private String postBody;

    private String postImageUrl;

    private int revision;
}
