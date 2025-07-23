package ru.practicum.ewm.main.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.main.dto.CommentDto;
import ru.practicum.ewm.main.dto.NewCommentDto;
import ru.practicum.ewm.main.dto.UpdateCommentDto;
import ru.practicum.ewm.main.service.CommentService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> createComment(
            @PathVariable Long userId,
            @RequestBody @Valid NewCommentDto dto) {
        log.info("PrivateCommentController - Creating comment by user {} for event {}", userId, dto.getEventId());
        CommentDto created = commentService.createComment(userId, dto);
        return ResponseEntity.status(201).body(created);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteOwnComment(
            @PathVariable Long userId,
            @PathVariable Long commentId) {
        log.info("PrivateCommentController - Deleting comment {} by user {}", commentId, userId);
        commentService.deleteOwnComment(userId, commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getUserComments(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        log.info("PrivateCommentController - Getting comments for user {}", userId);
        return ResponseEntity.ok(commentService.getUserComments(userId, from, size));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateOwnComment(
            @PathVariable Long userId,
            @PathVariable Long commentId,
            @RequestBody @Valid UpdateCommentDto updateDto) {
        log.info("PrivateCommentController - Updating comment {} by user {}", commentId, userId);
        return ResponseEntity.ok(commentService.updateOwnComment(userId, commentId, updateDto));
    }
}

