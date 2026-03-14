package com.mpj.ribbion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteResponse {
    private String targetType;
    private Long targetId;
    private Integer currentVoteCount;
    private Integer userVote; // +1, -1, or 0 (no vote)
}
