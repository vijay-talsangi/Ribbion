package com.mpj.ribbion.controller;

import com.mpj.ribbion.dto.*;
import com.mpj.ribbion.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<ApiResponse<QuestionResponse>> createQuestion(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody QuestionRequest request) {
        QuestionResponse response = questionService.createQuestion(userDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Question created", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<QuestionSummaryResponse>>> listQuestions(
            @RequestParam(defaultValue = "NEWEST") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(questionService.listQuestions(sort, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionResponse>> getQuestion(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(questionService.getQuestion(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionResponse>> updateQuestion(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody QuestionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(questionService.updateQuestion(userDetails, id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        questionService.deleteQuestion(userDetails, id);
        return ResponseEntity.ok(ApiResponse.success("Question deleted", null));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<QuestionSummaryResponse>>> searchQuestions(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(questionService.searchQuestions(q, page, size)));
    }

    @GetMapping("/tagged/{tag}")
    public ResponseEntity<ApiResponse<PagedResponse<QuestionSummaryResponse>>> getQuestionsByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(questionService.getQuestionsByTag(tag, page, size)));
    }
}
