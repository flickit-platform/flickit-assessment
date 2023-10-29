package org.flickit.flickitassessmentcore.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.flickitassessmentcore.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = PROJECT_ARTIFACT_ID)
public class AnnotationCheckArchUnitTest {

    @ArchTest
    private final ArchRule check_controllers_annotation =
        classes()
            .that()
            .resideInAPackage(ADAPTER_IN_REST)
            .and()
            .haveSimpleNameEndingWith(REST_CONTROLLER_SUFFIX)
            .should()
            .beAnnotatedWith(RestController.class);

    @ArchTest
    private final ArchRule check_service_annotation =
        classes()
            .that()
            .resideInAPackage(APPLICATION_SERVICE)
            .and()
            .haveSimpleNameEndingWith(SERVICE_SUFFIX)
            .and()
            .haveSimpleNameNotContaining(COLOR_SERVICE)
            .should(beAnnotatedWith(Service.class, Transactional.class));

    @ArchTest
    private final ArchRule check_persistence_annotation =
        classes()
            .that()
            .resideInAPackage(ADAPTER_OUT_PERSISTENCE)
            .and()
            .haveSimpleNameEndingWith(PERSISTENCE_JPA_ADAPTER_SUFFIX)
            .should()
            .beAnnotatedWith(Component.class);

    @ArchTest
    private final ArchRule check_entity_annotation =
        classes()
            .that()
            .resideInAPackage(ADAPTER_OUT_PERSISTENCE)
            .and()
            .haveSimpleNameEndingWith(JPA_ENTITY_SUFFIX)
            .should(beAnnotatedWith(Entity.class, Table.class));

    @ArchTest
    private final ArchRule check_adapter_annotation =
        classes()
            .that()
            .resideInAnyPackage(ADAPTER_OUT_CALCULATE, ADAPTER_OUT_REPORT)
            .and()
            .haveSimpleNameEndingWith(ADAPTER_SUFFIX)
            .should()
            .beAnnotatedWith(Component.class);

    @ArchTest
    private final ArchRule check_rest_adapter_annotation =
        classes()
            .that()
            .resideInAnyPackage(ADAPTER_OUT_REST)
            .and()
            .haveSimpleNameEndingWith(REST_ADAPTER_SUFFIX)
            .should(beAnnotatedWith(Component.class, Transactional.class));

    @ArchTest
    private final ArchRule check_service_test_annotation =
        classes()
            .that()
            .resideInAnyPackage(APPLICATION_SERVICE)
            .and()
            .haveSimpleNameEndingWith(SERVICE_TEST)
            .and()
            .haveSimpleNameNotContaining(COLOR_SERVICE)
            .should()
            .beAnnotatedWith(ExtendWith.class);

    @ArchTest
    private final ArchRule check_usecase_param_test_annotation =
        classes()
            .that()
            .resideInAnyPackage(APPLICATION_PORT_IN)
            .and()
            .haveSimpleNameEndingWith(USE_CASE_PARAM_TEST_SUFFIX)
            .should()
            .beAnnotatedWith(ExtendWith.class);

    private ArchCondition<JavaClass> beAnnotatedWith(Class<? extends Annotation>... annotationTypes) {
        String annotationDescription = Arrays.stream(annotationTypes).map(a -> "@" + a.getSimpleName()).collect(Collectors.joining(" "));
        return new ArchCondition<JavaClass>("be annotated with " + annotationDescription) {
            @Override
            public void check(JavaClass clazz, ConditionEvents events) {
                var annotations = List.of(clazz.reflect().getDeclaredAnnotations());
                var annotationTypesList = List.of(annotationTypes);
                boolean satisfied = annotations
                    .stream()
                    .map(Annotation::annotationType)
                    .toList()
                    .containsAll(annotationTypesList);
                if (!satisfied) {
                    events.add(SimpleConditionEvent.violated(clazz,
                        String.format("Class %s is not annotated with %s in %s",
                            clazz.getFullName(), annotationDescription, clazz.getSourceCodeLocation())));
                }
            }
        };
    }
}
