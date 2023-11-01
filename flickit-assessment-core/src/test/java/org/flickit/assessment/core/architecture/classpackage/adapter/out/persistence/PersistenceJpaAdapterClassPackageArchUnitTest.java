package org.flickit.assessment.core.architecture.classpackage.adapter.out.persistence;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.stereotype.Component;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {ADAPTER_FULL_PACKAGE}, importOptions = ImportOption.DoNotIncludeTests.class)
public class PersistenceJpaAdapterClassPackageArchUnitTest {

    @ArchTest
    private final ArchRule repository_and_mapper_should_be_in_adapter_out_persistence =
        classes()
            .that()
            .haveSimpleNameEndingWith(REPOSITORY_SUFFIX)
            .or()
            .haveSimpleNameEndingWith(MAPPER_SUFFIX)
            .should()
            .resideInAPackage(ADAPTER_OUT_PERSISTENCE);

    @ArchTest
    private final ArchRule persistence_adapter_should_be_in_adapter_out_persistence =
        classes()
            .that()
            .haveSimpleNameEndingWith(PERSISTENCE_JPA_ADAPTER_SUFFIX)
            .and()
            .areAnnotatedWith(Component.class)
            .should()
            .resideInAPackage(ADAPTER_OUT_PERSISTENCE);

    @ArchTest
    private final ArchRule entity_and_mapper_should_be_in_adapter_out_persistence =
        classes()
            .that()
            .haveSimpleNameEndingWith(ENTITY_SUFFIX)
            .and()
            .areAnnotatedWith(Entity.class)
            .and()
            .areAnnotatedWith(Table.class)
            .should()
            .resideInAPackage(ADAPTER_OUT_PERSISTENCE);

}
