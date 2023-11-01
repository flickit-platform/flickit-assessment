package org.flickit.assessment.core.architecture.namingconvention.application.port.out;

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {ADAPTER_FULL_PACKAGE, APPLICATION_FULL_PACKAGE}, importOptions = DoNotIncludeTests.class)
public class PortNamingConventionArchUnitTest {

    @ArchTest
    static ArchRule ports_should_be_suffixed_with_Port =
        classes()
            .that()
            .resideInAPackage(APPLICATION_PORT_OUT)
            .and()
            .areTopLevelClasses()
            .should()
            .haveSimpleNameEndingWith(PORT_SUFFIX);

}
