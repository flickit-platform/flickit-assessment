package org.flickit.assessment.core.architecture.classpackage.application.service;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {APPLICATION_SERVICE_FULL_PACKAGE})
public class ServiceClassPackageArchUnitTest {

    @ArchTest
    private final ArchRule all_services_should_be_in_application_service =
        classes()
            .that()
            .haveSimpleNameEndingWith(SERVICE_SUFFIX)
            .and()
            .areAnnotatedWith(Service.class)
            .should()
            .resideInAPackage(APPLICATION_SERVICE);

    @ArchTest
    private final ArchRule service_should_be_in_application_service =
        classes()
            .that()
            .haveSimpleNameEndingWith(SERVICE_SUFFIX)
            .and()
            .haveNameNotMatching(ENUM_SERVICE)
            .and()
            .areAnnotatedWith(Service.class)
            .and()
            .areAnnotatedWith(Transactional.class)
            .should()
            .resideInAPackage(APPLICATION_SERVICE);

    @ArchTest
    private final ArchRule service_test_should_be_in_application_service =
        classes()
            .that()
            .haveSimpleNameEndingWith(SERVICE_TEST_SUFFIX)
            .and()
            .haveNameNotMatching(ENUM_SERVICE)
            .and()
            .areAnnotatedWith(ExtendWith.class)
            .should()
            .resideInAPackage(APPLICATION_SERVICE);

}
