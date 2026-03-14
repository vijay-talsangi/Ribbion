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
public class CommentResponse {
    private Long id;
    private String body;
    private UserSummary author;
    private Long questionId;
    private Long answerId;
    private LocalDateTime createdAt;
}
