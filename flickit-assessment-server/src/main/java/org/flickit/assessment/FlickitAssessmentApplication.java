package org.flickit.assessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.flickit.assessment.common.util.AspectOrders.TRANSACTIONAL_ORDER;

@SpringBootApplication
@EnableTransactionManagement(order = TRANSACTIONAL_ORDER)
public class FlickitAssessmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlickitAssessmentApplication.class, args);
    }

}
