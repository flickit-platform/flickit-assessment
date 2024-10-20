package org.flickit.assessment.core.architecture.constants;

public class ArchUnitTestConstants {

    public static final String PROJECT_ARTIFACT_ID = "org.flickit.assessment.core";
    public static final String ROOT_SLICE = "..assessment.core.(*)..";

    // Full Package
    public static final String ADAPTER_FULL_PACKAGE = "org.flickit.assessment.core.adapter";
    public static final String APPLICATION_PORT_IN_FULL_PACKAGE = "org.flickit.assessment.core.application.port.in";
    public static final String APPLICATION_PORT_OUT_FULL_PACKAGE = "org.flickit.assessment.core.application.port.out";
    public static final String APPLICATION_PORT_FULL_PACKAGE = "org.flickit.assessment.core.application.port";
    public static final String APPLICATION_SERVICE_FULL_PACKAGE = "org.flickit.assessment.core.application.service";
    public static final String APPLICATION_DOMAIN_FULL_PACKAGE = "org.flickit.assessment.core.application.domain";
    public static final String ADAPTER_IN_REST_FULL_PACKAGE = "org.flickit.assessment.core.adapter.in.rest";
    public static final String APPLICATION_FULL_PACKAGE = "org.flickit.assessment.core.application";

    // Package
    public static final String ADAPTER_IN_REST = "..adapter.in.rest..";
    public static final String ADAPTER = "..adapter..";
    public static final String ADAPTER_OUT = "..adapter.out..";
    public static final String ADAPTER_OUT_PERSISTENCE = "..adapter.out.persistence..";
    public static final String APPLICATION_DOMAIN = "..application.domain..";
    public static final String APPLICATION_SERVICE = "..application.service..";
    public static final String ADAPTER_OUT_CALCULATE = "..adapter.out.calculate..";
    public static final String ADAPTER_OUT_REPORT = "..adapter.out.report..";
    public static final String ADAPTER_OUT_REST = "..adapter.out.rest..";
    public static final String ADAPTER_IN_REST_EXCEPTION = "..adapter.in.rest.exception..";
    public static final String APPLICATION_PORT_IN = "..application.port.in..";
    public static final String APPLICATION_PORT_OUT = "..application.port.out..";
    public static final String APPLICATION_SERVICE_CONSTANT = "..application.service.constant..";
    public static final String APPLICATION = "..assessment.core.application..";
    public static final String TEST_FIXTURE = "..test.fixture..";

    // Suffix
    public static final String REST_CONTROLLER_SUFFIX = "RestController";
    public static final String RESPONSE_DTO_SUFFIX = "ResponseDto";
    public static final String REQUEST_DTO_SUFFIX = "RequestDto";
    public static final String PERSISTENCE_JPA_ADAPTER_SUFFIX = "PersistenceJpaAdapter";
    public static final String ADAPTER_SUFFIX = "Adapter";
    public static final String REST_ADAPTER_SUFFIX = "RestAdapter";
    public static final String EXCEPTION_HANDLER_SUFFIX = "ExceptionHandler";
    public static final String LIST_USE_CASE_SUFFIX = "ListUseCase";
    public static final String USE_CASE_PARAM_TEST_SUFFIX = "UseCaseParamTest";
    public static final String SERVICE_TEST_SUFFIX = "ServiceTest";
    public static final String SERVICE_SUFFIX = "Service";
    public static final String ENUM_SERVICE = "(.*)(GetConfidenceLevelsService|GetEvidenceTypesService|GetAssessmentUserRolesService)";
    public static final String ENUM_SERVICE_TEST = "(.*)(GetConfidenceLevelsServiceTest|GetEvidenceTypesServiceTest|GetAssessmentUserRolesServiceTest)";
    public static final String USE_CASE_SUFFIX = "UseCase";
    public static final String PORT_SUFFIX = "Port";
    public static final String TEST_CLASS_SUFFIX = "Test";
    public static final String MOTHER_SUFFIX = "Mother";
    public static final String NOT_ARCH_UNIT_TEST_OR_MOTHER = "(.*)(ArchUnitTest|Mother)";
    public static final String TEST_METHOD_SUFFIX = "test";
}
