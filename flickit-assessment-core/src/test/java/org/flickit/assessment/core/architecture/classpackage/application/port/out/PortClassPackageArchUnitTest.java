package org.flickit.assessment.core.architecture.classpackage.application.port.out;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {APPLICATION_PORT_OUT_FULL_PACKAGE}, importOptions = ImportOption.DoNotIncludeTests.class)
public class PortClassPackageArchUnitTest {

    @ArchTest
    private final ArchRule port_should_be_in_application_port_out =
        classes()
            .that()
            .haveSimpleNameEndingWith(PORT_SUFFIX)
            .should()
            .resideInAPackage(APPLICATION_PORT_OUT);

}
