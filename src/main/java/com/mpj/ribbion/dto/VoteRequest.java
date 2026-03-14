package com.mpj.ribbion.dto;

import com.mpj.ribbion.entity.VoteTargetType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VoteRequest {
    @NotNull(message = "Target type is required")
    private VoteTargetType targetType;

    @NotNull(message = "Target ID is required")
    private Long targetId;

    @NotNull(message = "Vote value is required (1 or -1)")
    private Integer value;
}
