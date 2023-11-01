package org.flickit.assessment.core.architecture.classpackage.application.port.in;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {APPLICATION_PORT_IN_FULL_PACKAGE})
public class UseCaseClassPackageArchUnitTest {

    @ArchTest
    private final ArchRule useCase_should_be_in_application_port_in =
        classes()
            .that()
            .haveSimpleNameEndingWith(USE_CASE_SUFFIX)
            .should()
            .resideInAPackage(APPLICATION_PORT_IN);

    @ArchTest
    private final ArchRule useCase_test_should_be_in_application_port_in =
        classes()
            .that()
            .haveSimpleNameEndingWith(USE_CASE_TEST_SUFFIX)
            .and()
            .areAnnotatedWith(ExtendWith.class)
            .should()
            .resideInAPackage(APPLICATION_PORT_IN);

}
