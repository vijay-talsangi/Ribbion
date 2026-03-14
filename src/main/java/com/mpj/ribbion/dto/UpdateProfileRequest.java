package com.mpj.ribbion.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @Size(max = 50)
    private String displayName;

    private String avatarUrl;

    @Size(max = 500)
    private String bio;
}
