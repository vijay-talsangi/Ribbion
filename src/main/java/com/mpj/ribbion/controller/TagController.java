package com.mpj.ribbion.controller;

import com.mpj.ribbion.dto.ApiResponse;
import com.mpj.ribbion.dto.PagedResponse;
import com.mpj.ribbion.dto.TagResponse;
import com.mpj.ribbion.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<TagResponse>>> getAllTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(tagService.getAllTags(page, size)));
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<PagedResponse<TagResponse>>> getPopularTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(tagService.getPopularTags(page, size)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<TagResponse>>> searchTags(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(tagService.searchTags(q, page, size)));
    }
}
