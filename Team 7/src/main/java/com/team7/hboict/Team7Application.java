package com.team7.hboict;

import com.team7.hboict.service.DutchElectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class Team7Application {
    private static final Logger logger = LoggerFactory.getLogger(Team7Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Team7Application.class, args);
    }

    @Bean
    CommandLineRunner preloadElectionData(
            DutchElectionService dutchElectionService,
            @Value("${election.preload.ids:TK2023,TK2025}") String preloadIds,
            @Value("${election.preload.enabled:true}") boolean preloadEnabled
    ) {
        return args -> {
            if (!preloadEnabled) {
                logger.info("Election preload is disabled.");
                return;
            }

            Arrays.stream(preloadIds.split(","))
                    .map(String::trim)
                    .filter(id -> !id.isBlank())
                    .forEach(electionId -> {
                        try {
                            dutchElectionService.readResults(
                                    electionId,
                                    "electionData/" + electionId,
                                    null
                            );
                        } catch (Exception exception) {
                            logger.warn("Skipping preload for {} because startup preload failed: {}", electionId, exception.getMessage());
                        }
                    });
        };
    }
}
