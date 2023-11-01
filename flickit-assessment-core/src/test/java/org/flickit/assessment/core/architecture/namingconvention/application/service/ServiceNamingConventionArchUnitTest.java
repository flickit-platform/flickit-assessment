package org.flickit.assessment.core.architecture.namingconvention.application.service;

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {ADAPTER_FULL_PACKAGE, APPLICATION_FULL_PACKAGE}, importOptions = DoNotIncludeTests.class)
public class ServiceNamingConventionArchUnitTest {

    @ArchTest
    static ArchRule services_should_be_suffixed_with_Service =
        classes()
            .that()
            .resideInAPackage(APPLICATION_SERVICE)
            .and()
            .resideOutsideOfPackages(APPLICATION_SERVICE_CONSTANT)
            .and()
            .areAnnotatedWith(Service.class)
            .should()
            .haveSimpleNameEndingWith(SERVICE_SUFFIX);

}
