package com.mpj.ribbion.controller;

import com.mpj.ribbion.dto.*;
import com.mpj.ribbion.service.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping("/api/questions/{questionId}/answers")
    public ResponseEntity<ApiResponse<AnswerResponse>> createAnswer(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long questionId,
            @Valid @RequestBody AnswerRequest request) {
        AnswerResponse response = answerService.createAnswer(userDetails, questionId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Answer posted", response));
    }

    @GetMapping("/api/questions/{questionId}/answers")
    public ResponseEntity<ApiResponse<PagedResponse<AnswerResponse>>> getAnswersForQuestion(
            @PathVariable Long questionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(answerService.getAnswersForQuestion(questionId, page, size)));
    }

    @PutMapping("/api/answers/{id}")
    public ResponseEntity<ApiResponse<AnswerResponse>> updateAnswer(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody AnswerRequest request) {
        return ResponseEntity.ok(ApiResponse.success(answerService.updateAnswer(userDetails, id, request)));
    }

    @DeleteMapping("/api/answers/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAnswer(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        answerService.deleteAnswer(userDetails, id);
        return ResponseEntity.ok(ApiResponse.success("Answer deleted", null));
    }

    @PutMapping("/api/answers/{id}/accept")
    public ResponseEntity<ApiResponse<AnswerResponse>> acceptAnswer(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Answer accepted", answerService.acceptAnswer(userDetails, id)));
    }
}
