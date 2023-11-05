package org.flickit.assessment.core.architecture.classpackage.adapter.out.persistence;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Component;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {ADAPTER_FULL_PACKAGE}, importOptions = ImportOption.DoNotIncludeTests.class)
public class PersistenceJpaAdapterClassPackageArchUnitTest {

    @ArchTest
    private final ArchRule persistence_adapter_should_be_in_adapter_out_persistence =
        classes()
            .that()
            .haveSimpleNameEndingWith(PERSISTENCE_JPA_ADAPTER_SUFFIX)
            .and()
            .areAnnotatedWith(Component.class)
            .should()
            .resideInAPackage(ADAPTER_OUT_PERSISTENCE);
}
