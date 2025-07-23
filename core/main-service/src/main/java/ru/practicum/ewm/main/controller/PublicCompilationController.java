package ru.practicum.ewm.main.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.CompilationDto;
import ru.practicum.ewm.main.dto.params.CompilationParamsPublic;
import ru.practicum.ewm.main.service.CompilationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
@Slf4j
public class PublicCompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getCompilations(@ModelAttribute CompilationParamsPublic params) {
        log.info("Received request to get compilations with pinned = {}, from = {}, size = {}",
                params.getPinned(), params.getFrom(), params.getSize());

        List<CompilationDto> compilations = compilationService.getCompilations(params);
        log.info("Returning {} compilations", compilations.size());

        return ResponseEntity.ok(compilations);
    }

    @GetMapping("/{comp-id}")
    public ResponseEntity<CompilationDto> getCompilationById(@PathVariable("comp-id") Long compId) {
        log.info("Received request to get compilation with ID: {}", compId);
        CompilationDto compilationDto = compilationService.getCompilationById(compId);
        if (compilationDto == null) {
            log.warn("Compilation with ID: {} not found", compId);
            return ResponseEntity.notFound().build();
        }
        log.info("Returning compilation with ID: {}", compId);
        return ResponseEntity.ok(compilationDto);
    }
}