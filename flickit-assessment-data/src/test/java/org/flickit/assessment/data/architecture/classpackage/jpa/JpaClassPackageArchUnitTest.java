package org.flickit.assessment.data.architecture.classpackage.jpa;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.data.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {JPA_FULL_PACKAGE}, importOptions = ImportOption.DoNotIncludeTests.class)
public class JpaClassPackageArchUnitTest {

    @ArchTest
    private final ArchRule repository_should_be_in_data_jpa =
        classes()
            .that()
            .haveSimpleNameEndingWith(REPOSITORY_SUFFIX)
            .should()
            .resideInAPackage(JPA);

    @ArchTest
    private final ArchRule entity_should_be_in_data_jpa =
        classes()
            .that()
            .haveSimpleNameEndingWith(JPA_ENTITY_SUFFIX)
            .and()
            .areAnnotatedWith(Entity.class)
            .and()
            .areAnnotatedWith(Table.class)
            .should()
            .resideInAPackage(JPA);

}
