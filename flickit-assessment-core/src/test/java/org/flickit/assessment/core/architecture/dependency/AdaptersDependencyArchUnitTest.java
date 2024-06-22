package org.flickit.assessment.core.architecture.dependency;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {ADAPTER_FULL_PACKAGE}, importOptions = ImportOption.DoNotIncludeTests.class)
public class AdaptersDependencyArchUnitTest {

    @ArchTest
    static final ArchRule controllers_should_not_depend_other_other_adapters_and_out_ports_and_services =
        noClasses()
            .that()
            .resideInAPackage(ADAPTER_IN_REST)
            .and()
            .haveSimpleNameNotContaining(EXCEPTION_HANDLER_SUFFIX)
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                ADAPTER_OUT,
                APPLICATION_PORT_OUT,
                APPLICATION_SERVICE);

    @ArchTest
    static final ArchRule persistence_adapters_should_not_depend_adapters_and_services =
        noClasses()
            .that()
            .resideInAPackage(ADAPTER_OUT_PERSISTENCE)
            .and()
            .haveSimpleNameEndingWith(PERSISTENCE_JPA_ADAPTER_SUFFIX)
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                ADAPTER_IN_REST,
                ADAPTER_OUT_REST,
                ADAPTER_OUT_CALCULATE,
                ADAPTER_OUT_REPORT,
                APPLICATION_SERVICE
            );
}
