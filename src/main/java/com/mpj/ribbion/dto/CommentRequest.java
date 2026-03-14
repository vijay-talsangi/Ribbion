package com.mpj.ribbion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequest {
    @NotBlank(message = "Comment body is required")
    @Size(max = 1000, message = "Comment must be at most 1000 characters")
    private String body;
}
