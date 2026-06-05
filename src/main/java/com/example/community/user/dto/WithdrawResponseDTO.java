package com.example.community.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WithdrawResponseDTO {
    private LocalDateTime withDrawnAt;

    public WithdrawResponseDTO(LocalDateTime now) {
        this.withDrawnAt = now;
    }
}
