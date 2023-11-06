package org.flickit.assessment.data.architecture.namingconvention.jpa;

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.data.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {JPA_FULL_PACKAGE}, importOptions = DoNotIncludeTests.class)
public class PersistenceJpaAdapterNamingConventionArchUnitTest {

    @ArchTest
    static ArchRule entities_should_be_suffixed_with_JpaEntity =
        classes()
            .that()
            .resideInAPackage(JPA)
            .and()
            .areTopLevelClasses()
            .and()
            .areAnnotatedWith(Entity.class)
            .and()
            .areAnnotatedWith(Table.class)
            .should()
            .haveSimpleNameEndingWith(JPA_ENTITY_SUFFIX);

}
