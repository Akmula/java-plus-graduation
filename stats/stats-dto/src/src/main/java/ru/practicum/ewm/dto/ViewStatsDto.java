package ru.practicum.ewm.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ViewStatsDto {
    private String app;
    private String uri;
    private long hits;
}