package org.flickit.assessment.core.architecture.classpackage.adapter.out.calculate;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Component;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {ADAPTER_FULL_PACKAGE}, importOptions = ImportOption.DoNotIncludeTests.class)
public class CalculateAdapterClassPackageArchUnitTest {

    @ArchTest
    private final ArchRule calculate_adapter_should_be_in_adapter_out_calculate =
        classes()
            .that()
            .haveSimpleNameEndingWith(ADAPTER_SUFFIX)
            .and()
            .haveNameNotMatching("(.*)(" +
                REST_ADAPTER_SUFFIX + "|" +
                PERSISTENCE_JPA_ADAPTER_SUFFIX + "|" +
                REPORT_INFO_ADAPTOR_SUFFIX + ")")
            .and()
            .areAnnotatedWith(Component.class)
            .should()
            .resideInAnyPackage(ADAPTER_OUT_CALCULATE);

}
