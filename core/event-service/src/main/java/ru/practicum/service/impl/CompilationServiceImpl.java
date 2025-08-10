package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.dto.params.CompilationParamsPublic;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.CompilationService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    final CompilationRepository compilationRepository;
    final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getCompilations(CompilationParamsPublic params) {
        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());

        Page<Compilation> compilationsPage;
        if (params.getPinned() != null) {
            compilationsPage = compilationRepository.findAllByPinned(params.getPinned(), pageable);
        } else {
            compilationsPage = compilationRepository.findAll(pageable);
        }

        if (compilationsPage.isEmpty()) {
            return List.of();
        }

        return compilationsPage.getContent().stream()
                .map(compilation -> {
                    List<EventShortDto> eventShortDtos = compilation.getEvents().stream()
                            .map(EventMapper::toEventShortDtoFromEvent)
                            .collect(Collectors.toList());

                    return CompilationMapper.toCompilationDtoFromCompilation(compilation, eventShortDtos);
                })
                .collect(Collectors.toList());
    }


    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id=%d not found", compId)));

        List<EventShortDto> eventShortDtos = compilation.getEvents().stream()
                .map(EventMapper::toEventShortDtoFromEvent)
                .collect(Collectors.toList());

        return CompilationMapper.toCompilationDtoFromCompilation(compilation, eventShortDtos);
    }


    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        Optional<Compilation> existingCompilation = compilationRepository.findByTitle(newCompilationDto.getTitle());
        if (existingCompilation.isPresent()) {
            throw new ConflictException("Compilation with the title '" + newCompilationDto.getTitle() + "' already exists.");
        }
        Set<Event> events = new HashSet<>();

        if (newCompilationDto.getEvents() != null) {
            events.addAll(eventRepository.findAllById(newCompilationDto.getEvents()));
        }

        Compilation compilation = CompilationMapper.toCompilationFromNewCompilationDto(newCompilationDto, events);

        compilation = compilationRepository.save(compilation);

        List<EventShortDto> eventShortDtos = compilation.getEvents().stream().map(EventMapper::toEventShortDtoFromEvent).toList();

        return CompilationMapper.toCompilationDtoFromCompilation(compilation, eventShortDtos);
    }

    @Override
    @Transactional
    public void deleteById(Long compId) {

        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException(
                    String.format("Compilation with id=%d not found", compId));
        }
        compilationRepository.deleteById(compId);

    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest) {

        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id=%d not found", compId)));
        StringBuilder updatedFieldsLog = new StringBuilder();

        if (updateCompilationRequest.getTitle() != null &&
                !updateCompilationRequest.getTitle().equals(compilation.getTitle())) {
            Optional<Compilation> existingCompilation = compilationRepository.findByTitle(updateCompilationRequest.getTitle());
            if (existingCompilation.isPresent()) {
                throw new ConflictException("Compilation with the title '" + updateCompilationRequest.getTitle() + "' already exists.");
            }
            compilation.setTitle(updateCompilationRequest.getTitle());
            updatedFieldsLog.append("Title|");
        }

        if (updateCompilationRequest.getEvents() != null) {
            Set<Event> events = new HashSet<>(eventRepository.findAllById(updateCompilationRequest.getEvents()));
            compilation.setEvents(events);
            updatedFieldsLog.append("Events|");
        }

        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
            updatedFieldsLog.append("Pinned|");
        }

        compilationRepository.save(compilation);

        log.info("Updated fields for Compilation with id = {} : {}", compId, updatedFieldsLog);

        return CompilationMapper.toCompilationDtoFromCompilation(compilation, compilation.getEvents().stream()
                .map(EventMapper::toEventShortDtoFromEvent)
                .toList());
    }

}