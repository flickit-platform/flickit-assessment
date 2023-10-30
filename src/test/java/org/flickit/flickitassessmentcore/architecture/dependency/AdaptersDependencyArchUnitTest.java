package org.flickit.flickitassessmentcore.architecture.dependency;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.Location;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.conditions.ArchConditions;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.conditions.ArchConditions.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static org.flickit.flickitassessmentcore.architecture.constants.ArchUnitTestConstants.*;

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
                "..application.service.answer..",
                "..application.service.assessment..",
                "..application.service.assessmentcolor..",
                "..application.service.constant..",
                "..application.service.evidence..",
                "..application.service.questionnaire..",
                "..application.service.subject.."
            );

    @ArchTest
    static final ArchRule rest_out_adapters_should_not_depend_adapters_and_services_and_usecases =
        noClasses()
            .that()
            .resideInAPackage(ADAPTER_OUT_REST)
            .and()
            .haveSimpleNameEndingWith(REST_ADAPTER_SUFFIX)
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                ADAPTER_IN_REST,
                ADAPTER_OUT_PERSISTENCE,
                ADAPTER_OUT_CALCULATE,
                ADAPTER_OUT_REPORT,
                APPLICATION_SERVICE,
                APPLICATION_PORT_IN
            );

}
