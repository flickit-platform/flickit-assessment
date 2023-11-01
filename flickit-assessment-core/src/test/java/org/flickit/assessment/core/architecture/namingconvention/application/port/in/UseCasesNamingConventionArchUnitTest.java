package org.flickit.assessment.core.architecture.namingconvention.application.port.in;

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {ADAPTER_FULL_PACKAGE, APPLICATION_FULL_PACKAGE}, importOptions = DoNotIncludeTests.class)
public class UseCasesNamingConventionArchUnitTest {

    @ArchTest
    static ArchRule useCases_should_be_suffixed_with_UseCase =
        classes()
            .that()
            .resideInAPackage(APPLICATION_PORT_IN)
            .and()
            .areTopLevelClasses()
            .should()
            .haveSimpleNameEndingWith(USE_CASE_SUFFIX);

}
