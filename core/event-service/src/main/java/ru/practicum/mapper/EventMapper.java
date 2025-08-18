package ru.practicum.mapper;

import ru.practicum.dto.*;
import ru.practicum.model.*;
import ru.practicum.enums.EventState;

import java.time.LocalDateTime;

public class EventMapper {

    public static Event toEntity(NewEventDto dto, Long initiatorId) {
        return Event.builder()
                .title(dto.getTitle())
                .annotation(dto.getAnnotation())
                .description(dto.getDescription())
                .category(new Category(dto.getCategory(), null))
                .location(new Location(dto.getLocation().getLat(), dto.getLocation().getLon()))
                .eventDate(dto.getEventDate())
                .createdOn(LocalDateTime.now())
                .state(EventState.PENDING)
                .paid(dto.isPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.isRequestModeration())
                .initiatorId(initiatorId)
                .build();
    }

    public static EventShortDto toShortDto(Event event, long confirmed, double rating) {
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()))
                .paid(event.isPaid())
                .eventDate(event.getEventDate())
                .initiator(new UserShortDto(event.getInitiatorId(), null))
                .confirmedRequests(confirmed)
                .rating(rating)
                .build();
    }

    public static EventFullDto entityToFullDto(Event event, long confirmed, double rating, String name) {
        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .category(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()))
                .paid(event.isPaid())
                .eventDate(event.getEventDate())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .location(new LocationDto(event.getLocation().getLat(), event.getLocation().getLon()))
                .initiator(new UserShortDto(event.getInitiatorId(), name))
                .confirmedRequests(confirmed)
                .rating(rating)
                .build();
    }

    public static EventShortDto toEventShortDtoFromEvent(Event event) {
        CategoryDto category = new CategoryDto(event.getCategory().getId(), "Category Name");
        UserShortDto initiator = new UserShortDto(event.getInitiatorId(), "User Name");
        long confirmedRequests = 0L;
        double rating = 0;

        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(category)
                .confirmedRequests(confirmedRequests)
                .eventDate(event.getEventDate())
                .initiator(initiator)
                .paid(event.isPaid())
                .title(event.getTitle())
                .rating(rating)
                .build();
    }
}