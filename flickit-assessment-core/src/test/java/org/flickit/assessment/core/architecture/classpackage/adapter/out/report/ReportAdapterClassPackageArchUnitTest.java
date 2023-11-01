package org.flickit.assessment.core.architecture.classpackage.adapter.out.report;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {ADAPTER_FULL_PACKAGE}, importOptions = ImportOption.DoNotIncludeTests.class)
public class ReportAdapterClassPackageArchUnitTest {

    @ArchTest
    private final ArchRule report_adapter_should_be_in_adapter_out_report =
        classes()
            .that()
            .haveSimpleNameEndingWith(REPORT_INFO_ADAPTOR_SUFFIX)
            .and()
            .areAnnotatedWith(Component.class)
            .should()
            .resideInAnyPackage(ADAPTER_OUT_REPORT);

}
