package com.mpj.ribbion.service;

import com.mpj.ribbion.dto.*;
import com.mpj.ribbion.entity.Question;
import com.mpj.ribbion.entity.Tag;
import com.mpj.ribbion.entity.User;
import com.mpj.ribbion.exception.AccessDeniedException;
import com.mpj.ribbion.exception.ResourceNotFoundException;
import com.mpj.ribbion.repository.QuestionRepository;
import com.mpj.ribbion.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final TagRepository tagRepository;
    private final UserService userService;

    @Transactional
    public QuestionResponse createQuestion(UserDetails userDetails, QuestionRequest request) {
        User author = userService.getCurrentUser(userDetails);

        Set<Tag> tags = resolveAndCreateTags(request.getTagNames());

        Question question = Question.builder()
                .title(request.getTitle())
                .body(request.getBody())
                .author(author)
                .tags(tags)
                .build();

        question = questionRepository.save(question);

        // Update tag question counts
        for (Tag tag : tags) {
            tag.setQuestionCount(tag.getQuestionCount() + 1);
            tagRepository.save(tag);
        }

        return mapToResponse(question);
    }

    public QuestionResponse getQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        // Increment view count
        question.setViewCount(question.getViewCount() + 1);
        questionRepository.save(question);

        return mapToResponse(question);
    }

    public PagedResponse<QuestionSummaryResponse> listQuestions(String sort, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Question> questionPage;

        questionPage = switch (sort != null ? sort.toUpperCase() : "NEWEST") {
            case "VOTES" -> questionRepository.findAllByOrderByVoteCountDesc(pageable);
            case "VIEWS" -> questionRepository.findAllByOrderByViewCountDesc(pageable);
            case "UNANSWERED" -> questionRepository.findUnanswered(pageable);
            default -> questionRepository.findAllByOrderByCreatedAtDesc(pageable);
        };

        return mapToPagedSummary(questionPage);
    }

    public PagedResponse<QuestionSummaryResponse> searchQuestions(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Question> questionPage = questionRepository.searchByKeyword(keyword, pageable);
        return mapToPagedSummary(questionPage);
    }

    public PagedResponse<QuestionSummaryResponse> getQuestionsByTag(String tagName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Question> questionPage = questionRepository.findByTagName(tagName.toLowerCase(), pageable);
        return mapToPagedSummary(questionPage);
    }

    public PagedResponse<QuestionSummaryResponse> getQuestionsByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Question> questionPage = questionRepository.findByAuthorId(userId, pageable);
        return mapToPagedSummary(questionPage);
    }

    @Transactional
    public QuestionResponse updateQuestion(UserDetails userDetails, Long questionId, QuestionRequest request) {
        User user = userService.getCurrentUser(userDetails);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        if (!question.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only edit your own questions");
        }

        question.setTitle(request.getTitle());
        question.setBody(request.getBody());

        if (request.getTagNames() != null) {
            // Decrement old tag counts
            for (Tag tag : question.getTags()) {
                tag.setQuestionCount(Math.max(0, tag.getQuestionCount() - 1));
                tagRepository.save(tag);
            }

            Set<Tag> newTags = resolveAndCreateTags(request.getTagNames());
            question.setTags(newTags);

            // Increment new tag counts
            for (Tag tag : newTags) {
                tag.setQuestionCount(tag.getQuestionCount() + 1);
                tagRepository.save(tag);
            }
        }

        question = questionRepository.save(question);
        return mapToResponse(question);
    }

    @Transactional
    public void deleteQuestion(UserDetails userDetails, Long questionId) {
        User user = userService.getCurrentUser(userDetails);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        if (!question.getAuthor().getId().equals(user.getId())
                && !user.getRole().name().equals("ADMIN")) {
            throw new AccessDeniedException("You can only delete your own questions");
        }

        // Decrement tag counts
        for (Tag tag : question.getTags()) {
            tag.setQuestionCount(Math.max(0, tag.getQuestionCount() - 1));
            tagRepository.save(tag);
        }

        questionRepository.delete(question);
    }

    // --- Helper methods ---

    private Set<Tag> resolveAndCreateTags(Set<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new HashSet<>();
        }

        Set<String> normalizedNames = tagNames.stream()
                .map(String::toLowerCase)
                .map(String::trim)
                .collect(Collectors.toSet());

        Set<Tag> existingTags = new HashSet<>(tagRepository.findByNameInIgnoreCase(normalizedNames));
        Set<String> existingNames = existingTags.stream().map(Tag::getName).collect(Collectors.toSet());

        for (String name : normalizedNames) {
            if (!existingNames.contains(name)) {
                Tag newTag = Tag.builder().name(name).build();
                existingTags.add(tagRepository.save(newTag));
            }
        }

        return existingTags;
    }

    public static QuestionResponse mapToResponse(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .body(question.getBody())
                .author(AuthService.mapToUserSummary(question.getAuthor()))
                .tags(question.getTags().stream().map(QuestionService::mapTagToResponse).collect(Collectors.toSet()))
                .voteCount(question.getVoteCount())
                .viewCount(question.getViewCount())
                .answerCount(question.getAnswerCount())
                .status(question.getStatus().name())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }

    private static QuestionSummaryResponse mapToSummary(Question question) {
        return QuestionSummaryResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .author(AuthService.mapToUserSummary(question.getAuthor()))
                .tags(question.getTags().stream().map(QuestionService::mapTagToResponse).collect(Collectors.toSet()))
                .voteCount(question.getVoteCount())
                .viewCount(question.getViewCount())
                .answerCount(question.getAnswerCount())
                .status(question.getStatus().name())
                .createdAt(question.getCreatedAt())
                .build();
    }

    private PagedResponse<QuestionSummaryResponse> mapToPagedSummary(Page<Question> page) {
        return PagedResponse.<QuestionSummaryResponse>builder()
                .content(page.getContent().stream().map(QuestionService::mapToSummary).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    static TagResponse mapTagToResponse(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .description(tag.getDescription())
                .questionCount(tag.getQuestionCount())
                .build();
    }
}
