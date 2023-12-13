package org.flickit.assessment.core.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.springframework.http.ResponseEntity;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {APPLICATION_PORT_IN_FULL_PACKAGE, ADAPTER_IN_REST_FULL_PACKAGE})
public class MethodOutputArchUnitTest {

    @ArchTest
    static final ArchRule list_useCases_methods_should_have_paginated_response_output =
        methods()
            .that()
            .areDeclaredInClassesThat()
            .haveSimpleNameEndingWith(LIST_USE_CASE_SUFFIX)
            .should()
            .haveRawReturnType(PaginatedResponse.class);

    @ArchTest
    static final ArchRule list_controllers_methods_should_have_paginated_response_output =
        methods()
            .that()
            .areDeclaredInClassesThat()
            .haveSimpleNameEndingWith(REST_CONTROLLER_SUFFIX)
            .and()
            .arePublic()
            .should()
            .haveRawReturnType(ResponseEntity.class);

}
