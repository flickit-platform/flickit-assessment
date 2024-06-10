package org.flickit.assessment.kit.architecture.namingconvention.adapter.out.rest;

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Component;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.kit.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {ADAPTER_FULL_PACKAGE}, importOptions = DoNotIncludeTests.class)
public class RestAdapterNamingConventionArchUnitTest {

    @ArchTest
    static ArchRule rest_adapters_should_be_suffixed_with_Adapter =
        classes()
            .that()
            .resideInAnyPackage(ADAPTER_OUT_REST)
            .and()
            .areTopLevelClasses()
            .and()
            .areAnnotatedWith(Component.class)
            .should()
            .haveSimpleNameEndingWith(ADAPTER_SUFFIX);

}
