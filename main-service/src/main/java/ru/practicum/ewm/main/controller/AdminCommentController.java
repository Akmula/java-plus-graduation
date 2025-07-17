package ru.practicum.ewm.main.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.main.dto.CommentDto;
import ru.practicum.ewm.main.dto.params.CommentSearchParamsAdmin;
import ru.practicum.ewm.main.service.CommentService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class AdminCommentController {

    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        log.info("AdminCommentController - Deleting comment {}", commentId);
        commentService.deleteByAdmin(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getCommentsByAdmin(@ModelAttribute CommentSearchParamsAdmin params) {
        log.info("AdminCommentController - Getting comments with filters: {}", params);
        return ResponseEntity.ok(commentService.getAllByAdmin(params));
    }
}