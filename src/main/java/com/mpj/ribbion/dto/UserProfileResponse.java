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
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String displayName;
    private String avatarUrl;
    private String bio;
    private Integer reputation;
    private String role;
    private LocalDateTime createdAt;
}
