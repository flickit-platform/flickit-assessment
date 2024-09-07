package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AssessmentAnalysisInsight {

    public final String languagesInfo;
    public final String languagesUsage;
    public final String codeQuality;
    public final String codeComplexityDiagramText;
    public final String codeComplexity;
    public final String codeSmellDiagramText;
    public final String codeSmell;
    public final String codeDuplication;
    public final String codeOrganization;
    public final String codeReliabilityDiagramText;
    public final String codeReliability;
    public final String codeSecurityDiagramText;
    public final String codeSecurity;
    public final String testCoverageDiagramText;
    public final String testCoverage;
    public final String thirdPartyLibs;
}
