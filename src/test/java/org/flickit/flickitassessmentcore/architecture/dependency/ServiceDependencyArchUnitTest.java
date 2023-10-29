package org.flickit.flickitassessmentcore.architecture.dependency;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.flickitassessmentcore.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {
    APPLICATION_SERVICE_FULL_PACKAGE,
}, importOptions = ImportOption.DoNotIncludeTests.class)
public class ServiceDependencyArchUnitTest {

    @ArchTest
    static final ArchRule services_should_depend_domain_models_and_ports =
        classes()
            .that()
            .resideInAPackage(APPLICATION_SERVICE)
            .should()
            .onlyDependOnClassesThat()
            .resideInAnyPackage(APPLICATION_PORT_IN, APPLICATION_PORT_OUT, APPLICATION_DOMAIN, APPLICATION_SERVICE, JAVA, SLF4J, SPRING_FRAMEWORK);
}
