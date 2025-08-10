package ru.practicum.service;

import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.params.CompilationParamsPublic;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getCompilations(CompilationParamsPublic params);

    CompilationDto getCompilationById(Long compId);

    CompilationDto create(NewCompilationDto newCompilationDto);

    void deleteById(Long compId);

    CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest);
}