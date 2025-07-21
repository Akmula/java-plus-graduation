package ru.practicum.ewm.main.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.CompilationDto;
import ru.practicum.ewm.main.dto.NewCompilationDto;
import ru.practicum.ewm.main.dto.UpdateCompilationRequest;
import ru.practicum.ewm.main.service.CompilationService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> create(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Received request to create compilation: {}", newCompilationDto);
        CompilationDto compilationDto = compilationService.create(newCompilationDto);
        log.info("Compilation created with ID: {}", compilationDto.getId());
        return new ResponseEntity<>(compilationDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{comp-id}")
    public ResponseEntity<Void> deleteById(@PathVariable("comp-id") Long compId) {
        log.info("Received request to delete compilation with ID: {}", compId);
        compilationService.deleteById(compId);
        log.info("Compilation with ID: {} has been deleted", compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{comp-id}")
    public ResponseEntity<CompilationDto> update(@PathVariable("comp-id") Long compId,
                                                 @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.info("Received request to update compilation with ID: {}", compId);
        CompilationDto updatedCompilation = compilationService.update(compId, updateCompilationRequest);
        log.info("Compilation with ID: {} has been updated", compId);
        return new ResponseEntity<>(updatedCompilation, HttpStatus.OK);
    }
}