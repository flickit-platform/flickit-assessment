package org.flickit.assessment.kit.architecture.classpackage.adapter.out.rest;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.kit.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {ADAPTER_FULL_PACKAGE}, importOptions = ImportOption.DoNotIncludeTests.class)
public class RestAdapterClassPackageArchUnitTest {

    @ArchTest
    private final ArchRule rest_adapter_should_be_in_adapter_out_rest =
        classes()
            .that()
            .haveSimpleNameEndingWith(REST_ADAPTER_SUFFIX)
            .and()
            .areAnnotatedWith(Component.class)
            .and()
            .areAnnotatedWith(Transactional.class)
            .should()
            .resideInAPackage(ADAPTER_OUT_REST);
}
