package ru.practicum.ewm.main.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationFullDto {
    private Long id;
    private String title;
    private boolean pinned;
    private Set<EventShortDto> events;
}