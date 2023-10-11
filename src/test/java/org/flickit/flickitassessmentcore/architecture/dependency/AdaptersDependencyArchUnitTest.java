package org.flickit.flickitassessmentcore.architecture.dependency;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.flickit.flickitassessmentcore.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {
    ADAPTER_FULL_PACKAGE
})
public class AdaptersDependencyArchUnitTest {

    @ArchTest
    static final ArchRule controllers_should_access_usecases =
        classes()
            .that()
            .resideInAPackage(ADAPTER_IN_REST)
            .and()
            .haveSimpleNameEndingWith(REST_CONTROLLER_SUFFIX)
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(ADAPTER_IN_REST, APPLICATION_PORT_IN, APPLICATION_DOMAIN);

    @ArchTest
    static final ArchRule controllers_should_not_access_other_classes_than_usecases =
        noClasses()
            .that()
            .resideInAPackage(ADAPTER_IN_REST)
            .and()
            .haveSimpleNameNotContaining(EXCEPTION_HANDLER_SUFFIX)
            .should()
            .onlyDependOnClassesThat()
            .resideInAnyPackage(ADAPTER_OUT, APPLICATION_PORT_OUT, APPLICATION_SERVICE);

    @ArchTest
    static final ArchRule persistence_adapter_should_access_adapter_out_persistence_and_usecases =
        classes()
            .that()
            .resideInAPackage(ADAPTER_OUT_PERSISTENCE)
            .and()
            .haveSimpleNameEndingWith(PERSISTENCE_JPA_ADAPTER_SUFFIX)
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(ADAPTER_OUT_PERSISTENCE, APPLICATION_PORT_IN);

    @ArchTest
    static final ArchRule classes_in_adapter_out_rest_should_port_out_and_domain_model =
        classes()
            .that()
            .resideInAPackage(ADAPTER_OUT_REST)
            .and()
            .haveSimpleNameEndingWith(REST_ADAPTER_SUFFIX)
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(APPLICATION_PORT_OUT, APPLICATION_DOMAIN, SPRING_FRAMEWORK);

}
