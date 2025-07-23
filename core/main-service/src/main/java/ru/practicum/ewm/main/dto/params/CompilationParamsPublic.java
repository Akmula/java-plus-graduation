package ru.practicum.ewm.main.dto.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationParamsPublic {
    private Boolean pinned;
    private int from = PaginationDefaults.DEFAULT_FROM;
    private int size = PaginationDefaults.DEFAULT_SIZE;
}