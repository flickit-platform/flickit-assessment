package org.flickit.assessment.core.architecture.dependency;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {
    APPLICATION_PORT_IN_FULL_PACKAGE,
    APPLICATION_PORT_OUT_FULL_PACKAGE
}, importOptions = ImportOption.DoNotIncludeTests.class)
public class PortDependencyArchUnitTest {

    @ArchTest
    static final ArchRule useCases_should_not_depend_anything_other_than_domain_models =
        noClasses()
            .that()
            .resideInAPackage(APPLICATION_PORT_IN)
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(APPLICATION_PORT_OUT, APPLICATION_SERVICE, ADAPTER);

    @ArchTest
    static final ArchRule out_ports_should_not_depend_services_and_adapters =
        noClasses()
            .that()
            .resideInAPackage(APPLICATION_PORT_OUT)
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(APPLICATION_SERVICE, ADAPTER);
}
