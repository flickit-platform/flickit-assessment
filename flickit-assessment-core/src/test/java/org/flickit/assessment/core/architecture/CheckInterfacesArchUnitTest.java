package org.flickit.assessment.core.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import java.util.Set;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static java.util.stream.Collectors.joining;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = PROJECT_ARTIFACT_ID, importOptions = ImportOption.DoNotIncludeTests.class)
public class CheckInterfacesArchUnitTest {

    @ArchTest
    static final ArchRule all_ports_should_be_interface =
        classes()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAnyPackage(APPLICATION_PORT_IN, APPLICATION_PORT_OUT)
            .should()
            .beInterfaces();

    @ArchTest
    static final ArchRule no_service_related_interface_should_be_placed_in_service_packages =
        noClasses()
            .that()
            .resideInAPackage(APPLICATION_SERVICE)
            .should()
            .beInterfaces();

    @ArchTest
    static final ArchRule no_persistence_related_interface_should_be_placed_in_persistence_packages =
        noClasses()
            .that()
            .resideInAPackage(ADAPTER_OUT_PERSISTENCE)
            .and()
            .haveNameNotMatching(VIEW_OR_REPOSITORY_SUFFIX)
            .should()
            .beInterfaces();

    @ArchTest
    static final ArchRule useCases_should_have_one_implementation =
        classes()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAnyPackage(APPLICATION_PORT_IN)
            .should(haveAUniqueImplementation());


    private static ArchCondition<JavaClass> haveAUniqueImplementation() {
        return new ArchCondition<JavaClass>("have a unique implementation") {
            @Override
            public void check(JavaClass port, ConditionEvents events) {
                events.add(new SimpleConditionEvent(port,
                    port.getAllSubclasses().size() <= 1,
                    describe(port)));
            }

            private String describe(JavaClass port) {
                return String.format("%s is implemented by %s",
                    port.getSimpleName(), joinNamesOf(port.getAllSubclasses()));
            }

            private String joinNamesOf(Set<JavaClass> implementations) {
                if (implementations.isEmpty()) {
                    return "";
                }

                return implementations.stream().map(JavaClass::getSimpleName).collect(joining(", "));
            }
        };
    }


}
