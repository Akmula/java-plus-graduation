package ru.practicum.ewm.main.dto;

import lombok.*;
import ru.practicum.ewm.main.model.enums.RequestStatus;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds;

    private RequestStatus status;
}