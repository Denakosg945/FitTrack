package gr.hua.fitTrack.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import gr.hua.fitTrack.core.service.model.InitializationService;

@Component
public class InitializationRunner implements CommandLineRunner {

    private final InitializationService initializationService;

    public InitializationRunner(InitializationService initializationService) {
        this.initializationService = initializationService;
    }

    @Override
    public void run(String... args) {
        initializationService.populateDatabase();  // Runs AFTER Spring is fully ready
    }
}
