package org.flickit.assessment.kit.architecture.annotation.adapter.out.rest;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.kit.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = ADAPTER_FULL_PACKAGE)
public class RestAdaptersAnnotationCheckArchUnitTest {

    @ArchTest
    private final ArchRule rest_adapters_should_be_annotated_with_Component =
        classes()
            .that()
            .resideInAnyPackage(ADAPTER_OUT_REST)
            .and()
            .haveSimpleNameEndingWith(REST_ADAPTER_SUFFIX)
            .should()
            .beAnnotatedWith(Component.class);

    @ArchTest
    private final ArchRule rest_adapters_should_be_annotated_with_Transactional =
        classes()
            .that()
            .resideInAnyPackage(ADAPTER_OUT_REST)
            .and()
            .haveSimpleNameEndingWith(REST_ADAPTER_SUFFIX)
            .should()
            .beAnnotatedWith(Transactional.class);

}
