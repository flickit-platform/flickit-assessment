package org.flickit.assessment.core.architecture.namingconvention.adapter.in.rest;

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {ADAPTER_FULL_PACKAGE}, importOptions = DoNotIncludeTests.class)
public class RestControllerNamingConventionArchUnitTest {

    @ArchTest
    static ArchRule rest_controllers_should_be_suffixed_with_RestController =
        classes()
            .that()
            .resideInAPackage(ADAPTER_IN_REST)
            .and()
            .areAnnotatedWith(RestController.class)
            .should()
            .haveSimpleNameEndingWith(REST_CONTROLLER_SUFFIX);

    @ArchTest
    static ArchRule response_and_request_DTOs_should_be_suffixed_with_ResponseDto_and_RequestDto =
        classes()
            .that()
            .resideInAPackage(ADAPTER_IN_REST)
            .and()
            .areRecords()
            .and()
            .areNotNestedClasses()
            .should()
            .haveNameMatching("(.*)(" + RESPONSE_DTO_SUFFIX + "|" + REQUEST_DTO_SUFFIX + ")");


}
