package com.example.community.post.dto;

import com.example.community.post.entity.ReportReason;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportRequestDTO {
    @NotNull(message = "신고 사유를 선택해주세요.")
    private ReportReason reason;

    private String description;
}
