package org.flickit.assessment.core.architecture;

import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = PROJECT_ARTIFACT_ID, importOptions = ImportOption.OnlyIncludeTests.class)
public class UnitTestValidationArchUnitTest {

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
            .resideInAnyPackage(TEST_FIXTURE)
            .should()
            .haveSimpleNameEndingWith(MOTHER_SUFFIX);

    public ArchCondition<JavaMethod> callAnAssertionOrVerify =
        new ArchCondition<JavaMethod>("assert something") {
            @Override
            public void check(JavaMethod item, ConditionEvents events) {
                for (JavaMethodCall methodCall : item.getMethodCallsFromSelf()) {
                    if ((methodCall.getTargetOwner().getPackageName().equals(
                        org.junit.jupiter.api.Assertions.class.getPackageName())
                        && methodCall.getTargetOwner().getName().equals(
                        org.junit.jupiter.api.Assertions.class.getName()))
                        || (methodCall.getOriginOwner().getMethodCallsFromSelf().stream()
                        .anyMatch(mc -> mc.getTarget().getName().contains("verify")
                            && mc.getTarget().getFullName().contains(org.mockito.Mockito.class.getName())))) {
                        return;
                    }
                }
                events.add(SimpleConditionEvent.violated(
                    item, item.getDescription() + "does not assert or verify anything.")
                );
            }
        };
    @ArchTest
    private final ArchRule unit_tests_should_assert_or_verify =
        methods()
            .that()
            .areDeclaredInClassesThat()
            .haveNameNotMatching(NOT_ARCH_UNIT_TEST_OR_MOTHER)
            .and()
            .haveNameContaining(TEST_METHOD_SUFFIX)
            .should(callAnAssertionOrVerify);
    public ArchCondition<JavaMethod> callAnAssertThrows =
        new ArchCondition<JavaMethod>("assertThrow") {
            @Override
            public void check(JavaMethod item, ConditionEvents events) {
                for (JavaMethodCall methodCall : item.getMethodCallsFromSelf()) {
                    if (methodCall.getOriginOwner().getMethodCallsFromSelf().stream()
                        .anyMatch(mc -> mc.getTarget().getName().contains("assertThrows")
                            && mc.getTarget().getFullName().contains(org.junit.jupiter.api.Assertions.class.getName()))) {
                        return;
                    }
                }
                events.add(SimpleConditionEvent.violated(
                    item, item.getDescription() + "does not assert or verify anything.")
                );
            }
        };
    @ArchTest
    private final ArchRule unit_tests_that_checks_error_should_assert_throws =
        methods()
            .that()
            .haveNameContaining(ERROR_MESSAGE)
            .should(callAnAssertThrows);

}
