package org.flickit.assessment.core.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {PROJECT_ARTIFACT_ID}, importOptions = ImportOption.DoNotIncludeTests.class)
public class ClassPackageArchUnitTest {

    @ArchTest
    private final ArchRule rest_controller_should_be_in_adapter_in_rest =
        classes()
            .that()
            .haveSimpleNameEndingWith(REST_CONTROLLER_SUFFIX)
            .should()
            .resideInAPackage(ADAPTER_IN_REST);

    @ArchTest
    private final ArchRule persistence_and_repository_and_entity_and_mapper_should_be_in_adapter_out_persistence =
        classes()
            .that()
            .haveSimpleNameEndingWith(PERSISTENCE_JPA_ADAPTER_SUFFIX)
            .or()
            .haveSimpleNameEndingWith(ENTITY_SUFFIX)
            .or()
            .haveSimpleNameEndingWith(REPOSITORY_SUFFIX)
            .or()
            .haveSimpleNameEndingWith(MAPPER_SUFFIX)
            .should()
            .resideInAPackage(ADAPTER_OUT_PERSISTENCE);

    @ArchTest
    private final ArchRule service_should_be_in_application_service =
        classes()
            .that()
            .haveSimpleNameEndingWith(SERVICE_SUFFIX)
            .should()
            .resideInAPackage(APPLICATION_SERVICE);

    @ArchTest
    private final ArchRule useCase_should_be_in_application_port_in =
        classes()
            .that()
            .haveSimpleNameEndingWith(USE_CASE_SUFFIX)
            .should()
            .resideInAPackage(APPLICATION_PORT_IN);

    @ArchTest
    private final ArchRule port_should_be_in_application_port_out =
        classes()
            .that()
            .haveSimpleNameEndingWith(PORT_SUFFIX)
            .should()
            .resideInAPackage(APPLICATION_PORT_OUT);

    @ArchTest
    private final ArchRule adapter_should_be_in_adapter_out_calculate_and_report =
        classes()
            .that()
            .haveSimpleNameEndingWith(ADAPTER_SUFFIX)
            .and()
            .haveSimpleNameNotEndingWith(REST_ADAPTER_SUFFIX)
            .and()
            .haveSimpleNameNotEndingWith(PERSISTENCE_JPA_ADAPTER_SUFFIX)
            .should()
            .resideInAnyPackage(ADAPTER_OUT_CALCULATE, ADAPTER_OUT_REPORT);

    @ArchTest
    private final ArchRule rest_adapter_should_be_in_adapter_out_rest =
        classes()
            .that()
            .haveSimpleNameEndingWith(REST_ADAPTER_SUFFIX)
            .should()
            .resideInAPackage(ADAPTER_OUT_REST);
}
