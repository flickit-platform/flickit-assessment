package org.flickit.assessment.core.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.PROJECT_ARTIFACT_ID;
import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.ROOT_SLICE;

@AnalyzeClasses(packages = {PROJECT_ARTIFACT_ID}, importOptions = ImportOption.DoNotIncludeTests.class)
public class CyclicDependencyArchUnitTest {

    @ArchTest
    private final ArchRule no_classes_should_have_cyclic_dependency =
        slices()
            .matching(ROOT_SLICE)
            .should()
            .beFreeOfCycles();
}
