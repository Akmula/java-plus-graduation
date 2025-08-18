package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.practicum.processor.EventsSimilarityProcessor;
import ru.practicum.processor.UserActionProcessor;

@Component
@RequiredArgsConstructor
public class AnalyzerRunner implements CommandLineRunner {

    private final EventsSimilarityProcessor eventsSimilarityProcessor;
    private final UserActionProcessor userActionProcessor;

    @Override
    public void run(String... args) throws Exception {
        Thread userActionThread = new Thread(userActionProcessor);
        userActionThread.setName("UserActionHandlerThread");
        userActionThread.start();
        eventsSimilarityProcessor.start();
    }
}