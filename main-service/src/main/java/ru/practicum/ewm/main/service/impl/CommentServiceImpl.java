package ru.practicum.ewm.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.dto.CommentDto;
import ru.practicum.ewm.main.dto.NewCommentDto;
import ru.practicum.ewm.main.dto.UpdateCommentDto;
import ru.practicum.ewm.main.dto.params.CommentSearchParamsAdmin;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.exception.ValidationException;
import ru.practicum.ewm.main.mapper.CommentMapper;
import ru.practicum.ewm.main.model.Comment;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.model.User;
import ru.practicum.ewm.main.model.enums.EventState;
import ru.practicum.ewm.main.repository.CommentRepository;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.repository.UserRepository;
import ru.practicum.ewm.main.service.CommentService;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public CommentDto createComment(Long userId, NewCommentDto dto) {
        User user = getUserById(userId);

        Event event = getEventById(dto.getEventId());

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Can't comment unpublished events");
        }

        Comment comment = CommentMapper.toEntity(dto, user, event);
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public void deleteOwnComment(Long userId, Long commentId) {
        User user = getUserById(userId);

        Comment comment = getCommentById(commentId);

        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new ConflictException("User can delete only own comments");
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByEvent(Long eventId, int from, int size) {
        getEventById(eventId); // Проверка на существование события

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("createdOn").descending());
        return commentRepository.findByEvent_Id(eventId, pageable).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getUserComments(Long userId, int from, int size) {
        getUserById(userId); // Проверка на существование пользователя

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("createdOn").descending());
        return commentRepository.findByAuthor_Id(userId, pageable).stream()
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

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ValidationException("User can update only their own comment");
        }

        comment.setText(updateDto.getText());
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id " + commentId + " not found"));
    }
}