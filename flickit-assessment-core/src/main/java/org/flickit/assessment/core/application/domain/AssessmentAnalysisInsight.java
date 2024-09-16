package org.flickit.assessment.core.application.domain;

public record AssessmentAnalysisInsight(
    ProgrammingLanguages programmingLanguages,
    OverallCodeQuality overallCodeQuality,
    CodeComplexity codeComplexity,
    CodeSmell codeSmell,
    CodeDuplication codeDuplication,
    CodeOrganization codeOrganization,
    CodeReliability codeReliability,
    CodeSecurity codeSecurity,
    AutomatedTestCoverage automatedTestCoverage,
    ThirdPartyLibraries thirdPartyLibraries) {

    record ProgrammingLanguages(String info, String usage) {}

    record OverallCodeQuality(String text) {}

    record CodeComplexity(String figureCaption, String text) {}

    record CodeSmell(String figureCaption, String text) {}

    record CodeDuplication(String text) {}

    record CodeOrganization(String text) {}

    record CodeReliability(String figureCaption, String text) {}

    record CodeSecurity(String figureCaption, String text) {}

    record AutomatedTestCoverage(String figureCaption, String text) {}

    record ThirdPartyLibraries(String text) {}
}
