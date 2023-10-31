package org.flickit.assessment.core.architecture.dependency;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {
    ADAPTER_FULL_PACKAGE,
    APPLICATION_PORT_IN_FULL_PACKAGE,
    APPLICATION_PORT_OUT_FULL_PACKAGE,
    APPLICATION_SERVICE_FULL_PACKAGE,
    APPLICATION_DOMAIN_FULL_PACKAGE
}, importOptions = ImportOption.DoNotIncludeTests.class)
public class GeneralDependencyArchUnitTest {

    @ArchTest
    static final ArchRule architecture_layers_check =
        onionArchitecture()
            .adapter(REST_CONTROLLER_SUFFIX, ADAPTER_IN_REST)
            .adapter(ADAPTER_SUFFIX, ADAPTER_OUT)
            .domainModels(APPLICATION_DOMAIN)
            .domainServices(APPLICATION_PORT_IN, APPLICATION_PORT_OUT)
            .applicationServices(APPLICATION_SERVICE)
            .ignoreDependency(
                JavaClass.Predicates.resideInAPackage(
                    ADAPTER_IN_REST_EXCEPTION),
                DescribedPredicate.alwaysTrue())
            .withOptionalLayers(true);


}
