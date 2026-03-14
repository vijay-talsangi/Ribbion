package com.mpj.ribbion.controller;

import com.mpj.ribbion.dto.ApiResponse;
import com.mpj.ribbion.dto.VoteRequest;
import com.mpj.ribbion.dto.VoteResponse;
import com.mpj.ribbion.entity.VoteTargetType;
import com.mpj.ribbion.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<ApiResponse<VoteResponse>> castVote(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody VoteRequest request) {
        return ResponseEntity.ok(ApiResponse.success(voteService.castVote(userDetails, request)));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<VoteResponse>> getVoteStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam VoteTargetType targetType,
            @RequestParam Long targetId) {
        return ResponseEntity.ok(ApiResponse.success(voteService.getVoteStatus(userDetails, targetType, targetId)));
    }
}
