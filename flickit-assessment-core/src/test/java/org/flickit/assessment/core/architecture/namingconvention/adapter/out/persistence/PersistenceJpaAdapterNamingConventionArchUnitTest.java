package org.flickit.assessment.core.architecture.namingconvention.adapter.out.persistence;

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.stereotype.Component;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {ADAPTER_FULL_PACKAGE}, importOptions = DoNotIncludeTests.class)
public class PersistenceJpaAdapterNamingConventionArchUnitTest {

    @ArchTest
    static ArchRule persistence_jpa_adapters_should_be_suffixed_with_PersistenceJpaAdapter =
        classes()
            .that()
            .resideInAPackage(ADAPTER_OUT_PERSISTENCE)
            .and()
            .areTopLevelClasses()
            .and()
            .areAnnotatedWith(Component.class)
            .should()
            .haveSimpleNameEndingWith(PERSISTENCE_JPA_ADAPTER_SUFFIX);

    @ArchTest
    static ArchRule entities_should_be_suffixed_with_JpaEntity =
        classes()
            .that()
            .resideInAPackage(ADAPTER_OUT_PERSISTENCE)
            .and()
            .areTopLevelClasses()
            .and()
            .areAnnotatedWith(Entity.class)
            .and()
            .areAnnotatedWith(Table.class)
            .should()
            .haveSimpleNameEndingWith(JPA_ENTITY_SUFFIX);

}
