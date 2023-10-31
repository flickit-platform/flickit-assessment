package org.flickit.assessment.core.architecture;

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {ADAPTER_FULL_PACKAGE, APPLICATION_FULL_PACKAGE}, importOptions = DoNotIncludeTests.class)
public class NamingConventionArchUnitTest {

    @ArchTest
    static ArchRule rest_controllers_should_be_suffixed =
        classes()
            .that()
            .resideInAPackage(ADAPTER_IN_REST)
            .and()
            .haveSimpleNameNotContaining(EXCEPTION_HANDLER_SUFFIX)
            .and()
            .doNotHaveSimpleName(ERROR_CODES)
            .should()
            .haveNameMatching("(.*)(RestController|ResponseDto|RequestDto)");

    @ArchTest
    static ArchRule adapters_should_be_suffixed =
        classes()
            .that()
            .resideInAnyPackage(ADAPTER_OUT_CALCULATE, ADAPTER_OUT_REPORT)
            .and()
            .areTopLevelClasses()
            .should()
            .haveSimpleNameEndingWith(ADAPTER_SUFFIX);

    @ArchTest
    static ArchRule useCases_should_be_suffixed =
        classes()
            .that()
            .resideInAPackage(APPLICATION_PORT_IN)
            .and()
            .areTopLevelClasses()
            .should()
            .haveSimpleNameEndingWith(USE_CASE_SUFFIX);

    @ArchTest
    static ArchRule ports_should_be_suffixed =
        classes()
            .that()
            .resideInAPackage(APPLICATION_PORT_OUT)
            .and()
            .areTopLevelClasses()
            .should()
            .haveSimpleNameEndingWith(PORT_SUFFIX);

    @ArchTest
    static ArchRule services_should_be_suffixed =
        classes()
            .that()
            .resideInAPackage(APPLICATION_SERVICE)
            .and()
            .resideOutsideOfPackages(APPLICATION_SERVICE_CONSTANT, APPLICATION_SERVICE_EXCEPTION)
            .should()
            .haveSimpleNameEndingWith(SERVICE_SUFFIX);
}
