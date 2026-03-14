package com.mpj.ribbion.controller;

import com.mpj.ribbion.dto.*;
import com.mpj.ribbion.service.AnswerService;
import com.mpj.ribbion.service.QuestionService;
import com.mpj.ribbion.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final QuestionService questionService;
    private final AnswerService answerService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(userService.getProfile(userDetails)));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateProfile(userDetails, request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getProfileById(id)));
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<ApiResponse<PagedResponse<QuestionSummaryResponse>>> getUserQuestions(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(questionService.getQuestionsByUser(id, page, size)));
    }

    @GetMapping("/{id}/answers")
    public ResponseEntity<ApiResponse<PagedResponse<AnswerResponse>>> getUserAnswers(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(answerService.getAnswersByUser(id, page, size)));
    }
}
