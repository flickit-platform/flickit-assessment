package org.flickit.assessment.kit.architecture.constants;

public class ArchUnitTestConstants {

    public static final String PROJECT_ARTIFACT_ID = "org.flickit.assessment.kit";
    public static final String ROOT_SLICE = "..assessment.kit.(*)..";

    // Full Package
    public final static String ADAPTER_FULL_PACKAGE = "org.flickit.assessment.kit.adapter";
    public final static String APPLICATION_PORT_IN_FULL_PACKAGE = "org.flickit.assessment.kit.application.port.in";
    public final static String APPLICATION_PORT_OUT_FULL_PACKAGE = "org.flickit.assessment.kit.application.port.out";
    public final static String APPLICATION_PORT_FULL_PACKAGE = "org.flickit.assessment.kit.application.port";
    public final static String APPLICATION_SERVICE_FULL_PACKAGE = "org.flickit.assessment.kit.application.service";
    public final static String APPLICATION_DOMAIN_FULL_PACKAGE = "org.flickit.assessment.kit.application.domain";
    public final static String ADAPTER_IN_REST_FULL_PACKAGE = "org.flickit.assessment.kit.adapter.in.rest";
    public final static String APPLICATION_FULL_PACKAGE = "org.flickit.assessment.kit.application";

    // Package
    public final static String ADAPTER_IN_REST = "..adapter.in.rest..";
    public final static String ADAPTER = "..adapter..";
    public final static String ADAPTER_OUT = "..adapter.out..";
    public final static String ADAPTER_OUT_PERSISTENCE = "..adapter.out.persistence..";
    public final static String APPLICATION_DOMAIN = "..application.domain..";
    public final static String APPLICATION_SERVICE = "..application.service..";
    public final static String ADAPTER_OUT_CALCULATE = "..adapter.out.calculate..";
    public final static String ADAPTER_OUT_REPORT = "..adapter.out.report..";
    public final static String ADAPTER_OUT_REST = "..adapter.out.rest..";
    public final static String ADAPTER_IN_REST_EXCEPTION = "..adapter.in.rest.exception..";
    public final static String APPLICATION_PORT_IN = "..application.port.in..";
    public final static String APPLICATION_PORT_OUT = "..application.port.out..";
    public final static String APPLICATION_SERVICE_CONSTANT = "..application.service.constant..";
    public final static String APPLICATION = "..assessment.kit.application..";
    public final static String TEST_FIXTURE = "..test.fixture..";

    // Suffix
    public final static String REST_CONTROLLER_SUFFIX = "RestController";
    public final static String RESPONSE_DTO_SUFFIX = "ResponseDto";
    public final static String REQUEST_DTO_SUFFIX = "RequestDto";
    public final static String PERSISTENCE_JPA_ADAPTER_SUFFIX = "PersistenceJpaAdapter";
    public final static String ADAPTER_SUFFIX = "Adapter";
    public final static String REST_ADAPTER_SUFFIX = "RestAdapter";
    public final static String EXCEPTION_HANDLER_SUFFIX = "ExceptionHandler";
    public final static String LIST_USE_CASE_SUFFIX = "ListUseCase";
    public final static String USE_CASE_PARAM_TEST_SUFFIX = "UseCaseParamTest";
    public final static String SERVICE_TEST_SUFFIX = "ServiceTest";
    public final static String SERVICE_SUFFIX = "Service";
    public final static String USE_CASE_SUFFIX = "UseCase";
    public final static String PORT_SUFFIX = "Port";
    public static final String TEST_CLASS_SUFFIX = "Test";
    public static final String MOTHER_SUFFIX = "Mother";
    public static final String NOT_ARCH_UNIT_TEST_OR_MOTHER = "(.*)(ArchUnitTest|Mother)";
    public static final String TEST_METHOD_SUFFIX = "test";
}
