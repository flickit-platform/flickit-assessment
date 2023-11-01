package org.flickit.assessment.core.architecture.annotation.adapter.in.rest;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = ADAPTER_FULL_PACKAGE)
public class RestControllersAnnotationCheckArchUnitTest {

    @ArchTest
    private final ArchRule controllers_should_be_annotated_with_RestController =
        classes()
            .that()
            .resideInAPackage(ADAPTER_IN_REST)
            .and()
            .haveSimpleNameEndingWith(REST_CONTROLLER_SUFFIX)
            .should()
            .beAnnotatedWith(RestController.class);

}
