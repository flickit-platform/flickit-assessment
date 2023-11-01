package org.flickit.assessment.core.architecture.namingconvention;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = PROJECT_ARTIFACT_ID, importOptions = ImportOption.OnlyIncludeTests.class)
public class TestNamingConventionCheckArchUnitTest {

    @ArchTest
    private final ArchRule tests_should_have_name_ending_with_test =
        classes()
            .that()
            .resideInAnyPackage(APPLICATION, ADAPTER_OUT)
            .and()
            .areTopLevelClasses()
            .should()
            .haveSimpleNameEndingWith(TEST_CLASS_SUFFIX);

    @ArchTest
    private final ArchRule mothers_should_have_name_ending_with_mother =
        classes()
            .that()
            .resideInAPackage(TEST_FIXTURE)
            .should()
            .haveSimpleNameEndingWith(MOTHER_SUFFIX);

    @ArchTest
    private final ArchRule test_methods_should_have_name_starting_with_test =
        methods()
            .that()
            .areDeclaredInClassesThat()
            .haveNameNotMatching(NOT_ARCH_UNIT_TEST_OR_MOTHER)
            .and()
            .haveNameContaining(TEST_METHOD_SUFFIX)
            .should()
            .haveNameStartingWith(TEST_METHOD_SUFFIX);

}
