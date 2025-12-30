package gr.hua.fitTrack.config;

import gr.hua.fitTrack.core.service.TrainerService;
import gr.hua.fitTrack.core.service.model.InitializationService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class StartupRunner implements ApplicationListener<ContextRefreshedEvent> {

    private final InitializationService initializationService;

    public StartupRunner(InitializationService initializationService) {
        this.initializationService = initializationService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Hibernate has finished creating/updating tables at this point
        initializationService.populateDatabase();
    }
}
