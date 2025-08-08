package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.InternalEventFeignClient;
import ru.practicum.client.InternalUserFeignClient;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.dto.UpdateCommentDto;
import ru.practicum.dto.params.CommentSearchParamsAdmin;
import ru.practicum.entity.Comment;
import ru.practicum.enums.EventState;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.repository.CommentRepository;
import ru.practicum.service.CommentService;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final InternalEventFeignClient eventClient;
    private final InternalUserFeignClient userClient;

    @Override
    public CommentDto createComment(Long userId, NewCommentDto dto) {
        getUserById(userId);

        EventFullDto event = getEventById(dto.getEventId());
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Can't comment unpublished events");
        }

        Comment comment = CommentMapper.toEntity(dto, userId, event.getId());
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public void deleteOwnComment(Long userId, Long commentId) {
        getUserById(userId);

        Comment comment = getCommentById(commentId);

        if (!comment.getAuthorId().equals(userId)) {
            throw new ConflictException("User can delete only own comments");
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByEvent(Long eventId, int from, int size) {
        getEventById(eventId); // Проверка на существование события

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("createdOn").descending());
        return commentRepository.findByEventId(eventId, pageable).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getUserComments(Long userId, int from, int size) {
        getUserById(userId); // Проверка на существование пользователя

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("createdOn").descending());
        return commentRepository.findByAuthorId(userId, pageable).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByAdmin(Long commentId) {
        getCommentById(commentId); // проверка, что комментарий существует
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getAllByAdmin(CommentSearchParamsAdmin params) {
        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize(),
                Sort.by("createdOn").descending());

        // Проверка: существует ли автор (если указан)
        if (params.getAuthorId() != null) {
            getUserById(params.getAuthorId());
        }

        // Проверка: существует ли событие (если указано)
        if (params.getEventId() != null) {
            getEventById(params.getEventId());
        }

        LocalDateTime rangeStart = null;
        LocalDateTime rangeEnd = null;

        if (params.getRangeStart() != null) {
            try {
                rangeStart = LocalDateTime.parse(params.getRangeStart().replace(" ", "T"));
            } catch (DateTimeParseException e) {
                throw new ValidationException("Invalid rangeStart format, expected yyyy-MM-dd HH:mm:ss");
            }
        }

        if (params.getRangeEnd() != null) {
            try {
                rangeEnd = LocalDateTime.parse(params.getRangeEnd().replace(" ", "T"));
            } catch (DateTimeParseException e) {
                throw new ValidationException("Invalid rangeEnd format, expected yyyy-MM-dd HH:mm:ss");
            }
        }

        return commentRepository.findByFilters(
                        params.getAuthorId(),
                        params.getEventId(),
                        rangeStart,
                        rangeEnd,
                        pageable
                ).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto updateOwnComment(Long userId, Long commentId, UpdateCommentDto updateDto) {
        getUserById(userId); // проверка, что пользователь существует

        Comment comment = getCommentById(commentId); // переиспользуем метод

        if (!comment.getAuthorId().equals(userId)) {
            throw new ValidationException("User can update only their own comment");
        }

        comment.setText(updateDto.getText());
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    private void getUserById(Long userId) {
        userClient.getByUserId(userId);
    }

    private EventFullDto getEventById(Long eventId) {
        return eventClient.getEventByEventId(eventId);
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id " + commentId + " not found"));
    }
}