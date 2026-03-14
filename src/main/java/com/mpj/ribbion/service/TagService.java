package com.mpj.ribbion.service;

import com.mpj.ribbion.dto.PagedResponse;
import com.mpj.ribbion.dto.TagResponse;
import com.mpj.ribbion.entity.Tag;
import com.mpj.ribbion.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public PagedResponse<TagResponse> getAllTags(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Tag> tagPage = tagRepository.findAll(pageable);
        return mapToPagedResponse(tagPage);
    }

    public PagedResponse<TagResponse> getPopularTags(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Tag> tagPage = tagRepository.findPopularTags(pageable);
        return mapToPagedResponse(tagPage);
    }

    public PagedResponse<TagResponse> searchTags(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Tag> tagPage = tagRepository.searchByName(query, pageable);
        return mapToPagedResponse(tagPage);
    }

    private PagedResponse<TagResponse> mapToPagedResponse(Page<Tag> page) {
        return PagedResponse.<TagResponse>builder()
                .content(page.getContent().stream().map(QuestionService::mapTagToResponse).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
