package com.mpj.ribbion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponse {
    private Long id;
    private String body;
    private Long questionId;
    private UserSummary author;
    private Integer voteCount;
    private Boolean accepted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
