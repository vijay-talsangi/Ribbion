package com.mpj.ribbion.service;

import com.mpj.ribbion.dto.*;
import com.mpj.ribbion.entity.Answer;
import com.mpj.ribbion.entity.Question;
import com.mpj.ribbion.entity.QuestionStatus;
import com.mpj.ribbion.entity.User;
import com.mpj.ribbion.exception.AccessDeniedException;
import com.mpj.ribbion.exception.BadRequestException;
import com.mpj.ribbion.exception.ResourceNotFoundException;
import com.mpj.ribbion.repository.AnswerRepository;
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
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserService userService;

    @Transactional
    public AnswerResponse createAnswer(UserDetails userDetails, Long questionId, AnswerRequest request) {
        User author = userService.getCurrentUser(userDetails);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        Answer answer = Answer.builder()
                .body(request.getBody())
                .question(question)
                .author(author)
                .build();

        answer = answerRepository.save(answer);

        // Update answer count
        question.setAnswerCount(question.getAnswerCount() + 1);
        questionRepository.save(question);

        return mapToResponse(answer);
    }

    public PagedResponse<AnswerResponse> getAnswersForQuestion(Long questionId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Answer> answerPage = answerRepository.findByQuestionIdSorted(questionId, pageable);

        return PagedResponse.<AnswerResponse>builder()
                .content(answerPage.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .page(answerPage.getNumber())
                .size(answerPage.getSize())
                .totalElements(answerPage.getTotalElements())
                .totalPages(answerPage.getTotalPages())
                .last(answerPage.isLast())
                .build();
    }

    public PagedResponse<AnswerResponse> getAnswersByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Answer> answerPage = answerRepository.findByAuthorId(userId, pageable);

        return PagedResponse.<AnswerResponse>builder()
                .content(answerPage.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .page(answerPage.getNumber())
                .size(answerPage.getSize())
                .totalElements(answerPage.getTotalElements())
                .totalPages(answerPage.getTotalPages())
                .last(answerPage.isLast())
                .build();
    }

    @Transactional
    public AnswerResponse updateAnswer(UserDetails userDetails, Long answerId, AnswerRequest request) {
        User user = userService.getCurrentUser(userDetails);
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", answerId));

        if (!answer.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only edit your own answers");
        }

        answer.setBody(request.getBody());
        answer = answerRepository.save(answer);
        return mapToResponse(answer);
    }

    @Transactional
    public void deleteAnswer(UserDetails userDetails, Long answerId) {
        User user = userService.getCurrentUser(userDetails);
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", answerId));

        if (!answer.getAuthor().getId().equals(user.getId())
                && !user.getRole().name().equals("ADMIN")) {
            throw new AccessDeniedException("You can only delete your own answers");
        }

        Question question = answer.getQuestion();
        question.setAnswerCount(Math.max(0, question.getAnswerCount() - 1));

        if (answer.getAccepted()) {
            question.setStatus(QuestionStatus.OPEN);
        }

        questionRepository.save(question);
        answerRepository.delete(answer);
    }

    @Transactional
    public AnswerResponse acceptAnswer(UserDetails userDetails, Long answerId) {
        User user = userService.getCurrentUser(userDetails);
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", answerId));

        Question question = answer.getQuestion();

        if (!question.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("Only the question author can accept an answer");
        }

        if (answer.getAccepted()) {
            throw new BadRequestException("This answer is already accepted");
        }

        // Un-accept any previously accepted answer
        answerRepository.findByQuestionIdSorted(question.getId(), PageRequest.of(0, 100))
                .getContent()
                .stream()
                .filter(Answer::getAccepted)
                .forEach(a -> {
                    a.setAccepted(false);
                    answerRepository.save(a);
                });

        answer.setAccepted(true);
        answer = answerRepository.save(answer);

        question.setStatus(QuestionStatus.SOLVED);
        questionRepository.save(question);

        // Reward answerer with reputation
        User answerer = answer.getAuthor();
        answerer.setReputation(answerer.getReputation() + 15);

        return mapToResponse(answer);
    }

    private AnswerResponse mapToResponse(Answer answer) {
        return AnswerResponse.builder()
                .id(answer.getId())
                .body(answer.getBody())
                .questionId(answer.getQuestion().getId())
                .author(AuthService.mapToUserSummary(answer.getAuthor()))
                .voteCount(answer.getVoteCount())
                .accepted(answer.getAccepted())
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt())
                .build();
    }
}
