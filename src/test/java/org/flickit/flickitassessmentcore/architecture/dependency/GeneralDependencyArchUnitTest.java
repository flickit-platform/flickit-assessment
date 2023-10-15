package org.flickit.flickitassessmentcore.architecture.dependency;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;
import static com.tngtech.archunit.library.DependencyRules.NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES;
import static org.flickit.flickitassessmentcore.architecture.constants.ArchUnitTestConstants.*;

@AnalyzeClasses(packages = {
    ADAPTER_FULL_PACKAGE,
    APPLICATION_PORT_IN_FULL_PACKAGE,
    APPLICATION_PORT_OUT_FULL_PACKAGE,
    APPLICATION_SERVICE_FULL_PACKAGE,
}, importOptions = ImportOption.DoNotIncludeTests.class)
public class GeneralDependencyArchUnitTest {

    @ArchTest
    static final ArchRule no_accesses_to_upper_package = NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES;

    @ArchTest
    static final ArchRule architecture_layers_check =
        onionArchitecture()
            .adapter(REST_CONTROLLER_SUFFIX, ADAPTER_IN_REST)
            .adapter(PERSISTENCE_JPA_ADAPTER_SUFFIX, ADAPTER_OUT_PERSISTENCE)
            .domainModels(APPLICATION_DOMAIN)
            .domainServices(APPLICATION_SERVICE)
            .ignoreDependency(
                JavaClass.Predicates.resideInAnyPackage(
                    ADAPTER_OUT_CALCULATE,
                    ADAPTER_OUT_REPORT,
                    ADAPTER_IN_REST_EXCEPTION),
                DescribedPredicate.alwaysTrue())
            .withOptionalLayers(true);


}
