package com.mpj.ribbion.service;

import com.mpj.ribbion.dto.VoteRequest;
import com.mpj.ribbion.dto.VoteResponse;
import com.mpj.ribbion.entity.*;
import com.mpj.ribbion.exception.BadRequestException;
import com.mpj.ribbion.exception.ResourceNotFoundException;
import com.mpj.ribbion.repository.AnswerRepository;
import com.mpj.ribbion.repository.QuestionRepository;
import com.mpj.ribbion.repository.UserRepository;
import com.mpj.ribbion.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public VoteResponse castVote(UserDetails userDetails, VoteRequest request) {
        if (request.getValue() != 1 && request.getValue() != -1) {
            throw new BadRequestException("Vote value must be 1 (upvote) or -1 (downvote)");
        }

        User voter = userService.getCurrentUser(userDetails);

        // Prevent self-voting
        User targetAuthor = getTargetAuthor(request.getTargetType(), request.getTargetId());
        if (targetAuthor.getId().equals(voter.getId())) {
            throw new BadRequestException("You cannot vote on your own content");
        }

        Optional<Vote> existingVote = voteRepository.findByUserIdAndTargetTypeAndTargetId(
                voter.getId(), request.getTargetType(), request.getTargetId());

        int voteChange;

        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            if (vote.getValue().equals(request.getValue())) {
                // Same vote → remove it (toggle off)
                voteChange = -vote.getValue();
                voteRepository.delete(vote);
            } else {
                // Opposite vote → flip it
                voteChange = request.getValue() - vote.getValue(); // e.g., 1 - (-1) = 2
                vote.setValue(request.getValue());
                voteRepository.save(vote);
            }
        } else {
            // New vote
            Vote vote = Vote.builder()
                    .user(voter)
                    .targetType(request.getTargetType())
                    .targetId(request.getTargetId())
                    .value(request.getValue())
                    .build();
            voteRepository.save(vote);
            voteChange = request.getValue();
        }

        // Update denormalized vote count
        int currentVoteCount = updateVoteCount(request.getTargetType(), request.getTargetId(), voteChange);

        // Update target author's reputation
        targetAuthor.setReputation(targetAuthor.getReputation() + voteChange);
        userRepository.save(targetAuthor);

        // Get current user's vote status
        Integer userVote = voteRepository.findByUserIdAndTargetTypeAndTargetId(
                voter.getId(), request.getTargetType(), request.getTargetId())
                .map(Vote::getValue)
                .orElse(0);

        return VoteResponse.builder()
                .targetType(request.getTargetType().name())
                .targetId(request.getTargetId())
                .currentVoteCount(currentVoteCount)
                .userVote(userVote)
                .build();
    }

    public VoteResponse getVoteStatus(UserDetails userDetails, VoteTargetType targetType, Long targetId) {
        User voter = userService.getCurrentUser(userDetails);

        int currentVoteCount = getCurrentVoteCount(targetType, targetId);
        Integer userVote = voteRepository.findByUserIdAndTargetTypeAndTargetId(
                voter.getId(), targetType, targetId)
                .map(Vote::getValue)
                .orElse(0);

        return VoteResponse.builder()
                .targetType(targetType.name())
                .targetId(targetId)
                .currentVoteCount(currentVoteCount)
                .userVote(userVote)
                .build();
    }

    private User getTargetAuthor(VoteTargetType targetType, Long targetId) {
        return switch (targetType) {
            case QUESTION -> questionRepository.findById(targetId)
                    .orElseThrow(() -> new ResourceNotFoundException("Question", "id", targetId))
                    .getAuthor();
            case ANSWER -> answerRepository.findById(targetId)
                    .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", targetId))
                    .getAuthor();
        };
    }

    private int updateVoteCount(VoteTargetType targetType, Long targetId, int change) {
        return switch (targetType) {
            case QUESTION -> {
                Question q = questionRepository.findById(targetId)
                        .orElseThrow(() -> new ResourceNotFoundException("Question", "id", targetId));
                q.setVoteCount(q.getVoteCount() + change);
                questionRepository.save(q);
                yield q.getVoteCount();
            }
            case ANSWER -> {
                Answer a = answerRepository.findById(targetId)
                        .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", targetId));
                a.setVoteCount(a.getVoteCount() + change);
                answerRepository.save(a);
                yield a.getVoteCount();
            }
        };
    }

    private int getCurrentVoteCount(VoteTargetType targetType, Long targetId) {
        return switch (targetType) {
            case QUESTION -> questionRepository.findById(targetId)
                    .orElseThrow(() -> new ResourceNotFoundException("Question", "id", targetId))
                    .getVoteCount();
            case ANSWER -> answerRepository.findById(targetId)
                    .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", targetId))
                    .getVoteCount();
        };
    }
}
