package org.flickit.assessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.flickit.assessment.advice","org.flickit.assessment.kit"})
public class FlickitAssessmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlickitAssessmentApplication.class, args);
    }

}
