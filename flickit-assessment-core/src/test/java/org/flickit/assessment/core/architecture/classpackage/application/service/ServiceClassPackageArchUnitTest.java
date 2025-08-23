package org.flickit.assessment.core.architecture.classpackage.application.service;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {APPLICATION_SERVICE_FULL_PACKAGE}, importOptions = ImportOption.DoNotIncludeTests.class)
public class ServiceClassPackageArchUnitTest {

    @ArchTest
    private final ArchRule all_services_should_be_in_application_service =
        classes()
            .that()
            .haveSimpleNameEndingWith(SERVICE_SUFFIX)
            .and()
            .areAnnotatedWith(Service.class)
            .should()
            .resideInAPackage(APPLICATION_SERVICE);

}
