package ru.practicum.mapper;

import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.entity.Comment;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment toEntity(NewCommentDto dto, Long authorId, Long eventId) {
        return Comment.builder()
                .authorId(authorId)
                .eventId(eventId)
                .text(dto.getText())
                .createdOn(LocalDateTime.now())
                .build();
    }

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .authorId(comment.getAuthorId())
                .eventId(comment.getEventId())
                .text(comment.getText())
                .createdOn(comment.getCreatedOn())
                .build();
    }
}