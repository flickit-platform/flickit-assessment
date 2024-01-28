package org.flickit.assessment.advice.config;

import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.solver.SolverConfig;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.Question;
import org.flickit.assessment.advice.application.service.PlanConstraintProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ComponentScan("org.flickit.assessment.advice")
public class AdviceEnginAutoConfig {

    @Bean
    public SolverFactory<Plan> solverFactory(){
        return SolverFactory
            .create(new SolverConfig()
                .withSolutionClass(Plan.class)
                .withEntityClasses(Question.class)
                .withConstraintProviderClass(PlanConstraintProvider.class)
                // The solver runs only for 5 seconds on this small dataset.
                // It's recommended to run for at least 5 minutes ("5m") otherwise.
                .withTerminationSpentLimit(Duration.ofSeconds(2)));
    }
}
