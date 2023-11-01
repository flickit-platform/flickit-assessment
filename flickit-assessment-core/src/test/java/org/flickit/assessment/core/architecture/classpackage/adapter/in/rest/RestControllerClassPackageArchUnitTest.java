package org.flickit.assessment.core.architecture.classpackage.adapter.in.rest;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {ADAPTER_IN_REST_FULL_PACKAGE}, importOptions = ImportOption.DoNotIncludeTests.class)
public class RestControllerClassPackageArchUnitTest {

    @ArchTest
    private final ArchRule rest_controller_should_be_in_adapter_in_rest =
        classes()
            .that()
            .haveSimpleNameEndingWith(REST_CONTROLLER_SUFFIX)
            .and()
            .areAnnotatedWith(RestController.class)
            .should()
            .resideInAPackage(ADAPTER_IN_REST);

}
