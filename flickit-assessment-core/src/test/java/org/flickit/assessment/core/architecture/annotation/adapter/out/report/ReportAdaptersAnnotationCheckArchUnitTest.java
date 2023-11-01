package org.flickit.assessment.core.architecture.annotation.adapter.out.report;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Component;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = ADAPTER_FULL_PACKAGE)
public class ReportAdaptersAnnotationCheckArchUnitTest {

    @ArchTest
    private final ArchRule report_adapters_should_be_annotated_with_Component =
        classes()
            .that()
            .resideInAnyPackage(ADAPTER_OUT_REPORT)
            .and()
            .haveSimpleNameEndingWith(ADAPTER_SUFFIX)
            .should()
            .beAnnotatedWith(Component.class);


}
