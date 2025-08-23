package org.flickit.assessment.core.architecture.classpackage.application.port.in;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {APPLICATION_PORT_IN_FULL_PACKAGE}, importOptions = ImportOption.DoNotIncludeTests.class)
public class UseCaseClassPackageArchUnitTest {

    @ArchTest
    private final ArchRule useCase_should_be_in_application_port_in =
        classes()
            .that()
            .haveSimpleNameEndingWith(USE_CASE_SUFFIX)
            .should()
            .resideInAPackage(APPLICATION_PORT_IN);

}
