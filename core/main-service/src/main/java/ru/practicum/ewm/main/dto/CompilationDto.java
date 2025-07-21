package ru.practicum.ewm.main.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDto {

    private Long id;
    private String title;
    private List<EventShortDto> events;
    private boolean pinned;
}
