package com.mpj.ribbion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSummaryResponse {
    private Long id;
    private String title;
    private UserSummary author;
    private Set<TagResponse> tags;
    private Integer voteCount;
    private Integer viewCount;
    private Integer answerCount;
    private String status;
    private LocalDateTime createdAt;
}
