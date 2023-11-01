package org.flickit.assessment.core.architecture.annotation.application.port.in;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = APPLICATION_PORT_FULL_PACKAGE)
public class UseCasesAnnotationCheckArchUnitTest {

    @ArchTest
    private final ArchRule useCase_param_tests_should_be_annotated_with_ExtendWith =
        classes()
            .that()
            .resideInAPackage(APPLICATION_PORT_IN)
            .and()
            .haveSimpleNameEndingWith(USE_CASE_PARAM_TEST_SUFFIX)
            .should()
            .beAnnotatedWith(ExtendWith.class);

}
