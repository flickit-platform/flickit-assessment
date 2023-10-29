package org.flickit.flickitassessmentcore.architecture.dependency;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.flickitassessmentcore.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {
    APPLICATION_PORT_IN_FULL_PACKAGE,
    APPLICATION_PORT_OUT_FULL_PACKAGE
}, importOptions = ImportOption.DoNotIncludeTests.class)
public class PortDependencyArchUnitTest {

    @ArchTest
    static final ArchRule usecases_should_depend_domain_models =
        classes()
            .that()
            .resideInAPackage(APPLICATION_PORT_IN)
            .should()
            .onlyDependOnClassesThat()
            .resideInAnyPackage(APPLICATION_PORT_IN, APPLICATION_DOMAIN, COMMON, JAVA, JAKARTA_VALIDATION_CONSTRAINTS);

    @ArchTest
    static final ArchRule out_ports_should_depend_domain_model =
        classes()
            .that()
            .resideInAPackage(APPLICATION_PORT_OUT)
            .should()
            .onlyDependOnClassesThat()
            .resideInAnyPackage(APPLICATION_PORT_OUT, APPLICATION_PORT_IN, APPLICATION_DOMAIN, COMMON, JAVA);
}
