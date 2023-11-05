package org.flickit.assessment.core.architecture.annotation.adapter.out.persistence;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Component;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = ADAPTER_FULL_PACKAGE)
public class PersistenceJpaAdaptersAnnotationCheckArchUnitTest {

    @ArchTest
    private final ArchRule persistence_adapters_should_be_annotated_with_Component =
        classes()
            .that()
            .resideInAPackage(ADAPTER_OUT_PERSISTENCE)
            .and()
            .haveSimpleNameEndingWith(PERSISTENCE_JPA_ADAPTER_SUFFIX)
            .should()
            .beAnnotatedWith(Component.class);
}
