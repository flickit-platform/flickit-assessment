package org.flickit.assessment.core.architecture.dependency;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {
    APPLICATION_SERVICE_FULL_PACKAGE,
}, importOptions = ImportOption.DoNotIncludeTests.class)
public class ServiceDependencyArchUnitTest {

    @ArchTest
    static final ArchRule services_should_not_depend_adapters =
        noClasses()
            .that()
            .resideInAPackage(APPLICATION_SERVICE)
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(ADAPTER);

}
