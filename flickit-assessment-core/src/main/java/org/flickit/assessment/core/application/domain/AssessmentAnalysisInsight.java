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

    public record ProgrammingLanguages(String info, String usage) {}

    public record OverallCodeQuality(String text) {}

    public record CodeComplexity(String figureCaption, String text) {}

    public record CodeSmell(String figureCaption, String text) {}

    public record CodeDuplication(String text) {}

    public record CodeOrganization(String text) {}

    public record CodeReliability(String figureCaption, String text) {}

    public record CodeSecurity(String figureCaption, String text) {}

    public record AutomatedTestCoverage(String figureCaption, String text) {}

    public record ThirdPartyLibraries(String text) {}
}
