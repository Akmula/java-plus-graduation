package ru.practicum.service;

import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.dto.UpdateCommentDto;
import ru.practicum.dto.params.CommentSearchParamsAdmin;

import java.util.List;

public interface CommentService {

    CommentDto createComment(Long userId, NewCommentDto newCommentDto);

    void deleteOwnComment(Long userId, Long commentId);

    List<CommentDto> getCommentsByEvent(Long eventId, int from, int size);

    List<CommentDto> getUserComments(Long userId, int from, int size);

    void deleteByAdmin(Long commentId);

    List<CommentDto> getAllByAdmin(CommentSearchParamsAdmin params);

    CommentDto updateOwnComment(Long userId, Long commentId, UpdateCommentDto updateDto);
}
