package org.flickit.assessment.core.architecture.annotation.application.service;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = APPLICATION_SERVICE_FULL_PACKAGE)
public class ServicesAnnotationCheckArchUnitTest {

    @ArchTest
    private final ArchRule services_should_be_annotated_with_Service =
        classes()
            .that()
            .resideInAPackage(APPLICATION_SERVICE)
            .and()
            .haveSimpleNameEndingWith(SERVICE_SUFFIX)
            .should()
            .beAnnotatedWith(Service.class);

    @ArchTest
    private final ArchRule services_should_be_annotated_with_Transactional =
        classes()
            .that()
            .resideInAPackage(APPLICATION_SERVICE)
            .and()
            .haveSimpleNameEndingWith(SERVICE_SUFFIX)
            .and()
            .haveNameNotMatching(ENUM_SERVICE)
            .should()
            .beAnnotatedWith(Transactional.class);

    @ArchTest
    private final ArchRule service_tests_should_be_annotated_with_ExtendWith =
        classes()
            .that()
            .resideInAnyPackage(APPLICATION_SERVICE)
            .and()
            .haveSimpleNameEndingWith(SERVICE_TEST_SUFFIX)
            .and()
            .haveNameNotMatching(ENUM_SERVICE_TEST)
            .should()
            .beAnnotatedWith(ExtendWith.class);

}
