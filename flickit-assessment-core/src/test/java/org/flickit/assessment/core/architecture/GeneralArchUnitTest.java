package org.flickit.assessment.core.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;

import static org.flickit.assessment.core.architecture.constants.ArchUnitTestConstants.PROJECT_ARTIFACT_ID;

@AnalyzeClasses(packages = PROJECT_ARTIFACT_ID, importOptions = ImportOption.DoNotIncludeTests.class)
public class GeneralArchUnitTest {


}
