package org.flickit.assessment.kit.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.core.annotation.Order;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "org.flickit.assessment.kit")
public class ExceptionHandlersOrderAnnotationArchUnitTest {

    @ArchTest
    private final ArchRule exception_handlers_should_be_annotated_by_order =
        classes()
            .that()
            .haveSimpleNameEndingWith("ExceptionHandler")
            .should()
            .beAnnotatedWith(Order.class);
}
