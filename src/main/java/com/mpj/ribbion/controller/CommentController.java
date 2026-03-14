package com.mpj.ribbion.controller;

import com.mpj.ribbion.dto.*;
import com.mpj.ribbion.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/questions/{questionId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> addCommentToQuestion(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long questionId,
            @Valid @RequestBody CommentRequest request) {
        CommentResponse response = commentService.addCommentToQuestion(userDetails, questionId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Comment added", response));
    }

    @GetMapping("/api/questions/{questionId}/comments")
    public ResponseEntity<ApiResponse<PagedResponse<CommentResponse>>> getQuestionComments(
            @PathVariable Long questionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(commentService.getCommentsForQuestion(questionId, page, size)));
    }

    @PostMapping("/api/answers/{answerId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> addCommentToAnswer(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long answerId,
            @Valid @RequestBody CommentRequest request) {
        CommentResponse response = commentService.addCommentToAnswer(userDetails, answerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Comment added", response));
    }

    @GetMapping("/api/answers/{answerId}/comments")
    public ResponseEntity<ApiResponse<PagedResponse<CommentResponse>>> getAnswerComments(
            @PathVariable Long answerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(commentService.getCommentsForAnswer(answerId, page, size)));
    }

    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        commentService.deleteComment(userDetails, id);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted", null));
    }
}
