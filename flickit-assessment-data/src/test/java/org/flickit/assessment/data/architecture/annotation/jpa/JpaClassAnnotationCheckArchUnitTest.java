package org.flickit.assessment.data.architecture.annotation.jpa;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.data.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = JPA_FULL_PACKAGE)
public class JpaClassAnnotationCheckArchUnitTest {

    @ArchTest
    private final ArchRule entities_should_be_annotated_with_Entity =
        classes()
            .that()
            .resideInAPackage(JPA)
            .and()
            .haveSimpleNameEndingWith(JPA_ENTITY_SUFFIX)
            .should()
            .beAnnotatedWith(Entity.class);

    @ArchTest
    private final ArchRule entities_should_be_annotated_with_Table =
        classes()
            .that()
            .resideInAPackage(JPA)
            .and()
            .haveSimpleNameEndingWith(JPA_ENTITY_SUFFIX)
            .should()
            .beAnnotatedWith(Table.class);
}
