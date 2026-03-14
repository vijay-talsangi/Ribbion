package com.mpj.ribbion.service;

import com.mpj.ribbion.dto.*;
import com.mpj.ribbion.entity.Answer;
import com.mpj.ribbion.entity.Comment;
import com.mpj.ribbion.entity.Question;
import com.mpj.ribbion.entity.User;
import com.mpj.ribbion.exception.AccessDeniedException;
import com.mpj.ribbion.exception.ResourceNotFoundException;
import com.mpj.ribbion.repository.AnswerRepository;
import com.mpj.ribbion.repository.CommentRepository;
import com.mpj.ribbion.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserService userService;

    @Transactional
    public CommentResponse addCommentToQuestion(UserDetails userDetails, Long questionId, CommentRequest request) {
        User author = userService.getCurrentUser(userDetails);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        Comment comment = Comment.builder()
                .body(request.getBody())
                .author(author)
                .question(question)
                .build();

        comment = commentRepository.save(comment);
        return mapToResponse(comment);
    }

    @Transactional
    public CommentResponse addCommentToAnswer(UserDetails userDetails, Long answerId, CommentRequest request) {
        User author = userService.getCurrentUser(userDetails);
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", answerId));

        Comment comment = Comment.builder()
                .body(request.getBody())
                .author(author)
                .answer(answer)
                .build();

        comment = commentRepository.save(comment);
        return mapToResponse(comment);
    }

    public PagedResponse<CommentResponse> getCommentsForQuestion(Long questionId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findByQuestionIdOrderByCreatedAtAsc(questionId, pageable);
        return mapToPagedResponse(commentPage);
    }

    public PagedResponse<CommentResponse> getCommentsForAnswer(Long answerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findByAnswerIdOrderByCreatedAtAsc(answerId, pageable);
        return mapToPagedResponse(commentPage);
    }

    @Transactional
    public void deleteComment(UserDetails userDetails, Long commentId) {
        User user = userService.getCurrentUser(userDetails);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!comment.getAuthor().getId().equals(user.getId())
                && !user.getRole().name().equals("ADMIN")) {
            throw new AccessDeniedException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    private CommentResponse mapToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .body(comment.getBody())
                .author(AuthService.mapToUserSummary(comment.getAuthor()))
                .questionId(comment.getQuestion() != null ? comment.getQuestion().getId() : null)
                .answerId(comment.getAnswer() != null ? comment.getAnswer().getId() : null)
                .createdAt(comment.getCreatedAt())
                .build();
    }

    private PagedResponse<CommentResponse> mapToPagedResponse(Page<Comment> page) {
        return PagedResponse.<CommentResponse>builder()
                .content(page.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
