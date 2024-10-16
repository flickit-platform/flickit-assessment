package org.flickit.assessment.core.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.GeneralCodingRules.*;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.PROJECT_ARTIFACT_ID;

@AnalyzeClasses(packages = PROJECT_ARTIFACT_ID)
public class RegularCodingRulesArchUnitTest {

    @ArchTest
    private final ArchRule no_access_to_standard_streams = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

    @ArchTest
    private final ArchRule no_generic_exceptions = NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

    @ArchTest
    private final ArchRule no_java_util_logging = NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

    @ArchTest
    private final ArchRule no_jodaTime = NO_CLASSES_SHOULD_USE_JODATIME;

    @ArchTest
    private final ArchRule no_field_injection = NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

    @ArchTest
    private final ArchRule no_use_of_deprecated_api = DEPRECATED_API_SHOULD_NOT_BE_USED;

    @ArchTest
    private final ArchRule service_test_classes_should_reside_in_same_package_with_implementation =
        testClassesShouldResideInTheSamePackageAsImplementation("ServiceTest");

    @ArchTest
    private final ArchRule use_case_param_test_classes_should_reside_in_same_package_with_implementation =
        testClassesShouldResideInTheSamePackageAsImplementation("UseCaseParamTest");
}
