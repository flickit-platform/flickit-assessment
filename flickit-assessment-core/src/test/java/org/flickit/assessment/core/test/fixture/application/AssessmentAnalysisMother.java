package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.AnalysisType;
import org.flickit.assessment.core.application.domain.AssessmentAnalysis;

import java.time.LocalDateTime;
import java.util.UUID;

public class AssessmentAnalysisMother {

    private final static String AI_ANALYSIS = """
            {
                "automatedTestCoverage": {
                    "figureCaption": "Test Coverage Distribution",
                    "text": "The overall test coverage across the repositories shows variability, with some repositories having significant coverage percentages while others lack coverage entirely. The average weighted test coverage is approximately 23.51%, indicating areas for improvement in automated testing practices."
                },
                "codeComplexity": {
                    "figureCaption": "Cyclomatic and Cognitive Complexity",
                    "text": "The code complexity analysis reveals high cyclomatic complexity across the repositories, with a total of 6,663. Additionally, cognitive complexity is notable, especially in the flickit-assessment repository with a value of 611. Such complexity levels may affect maintainability and readability of the code."
                },
                "codeDuplication": {
                    "text": "Code duplication is present across the repositories, with the duplicated lines density averaging 3.42%. The flickit-platform and flickit-assessment repositories exhibit the highest levels of duplication, necessitating refactoring and optimization to reduce redundancy and improve code quality."
                },
                "codeOrganization": {
                    "text": "The code organization analysis requires manual input and external tools to assess package layout and structure consistency. Ensuring a well-structured codebase with consistent organization can enhance readability and maintainability, aiding in efficient project management and development."
                },
                "codeReliability": {
                    "figureCaption": "Reliability Metrics",
                    "text": "Code reliability across the repositories varies, with the flickit-platform repository receiving a 'D' grade due to a higher count of reliability issues. Other repositories maintain an 'A' grade with minimal to no reliability issues. Focused efforts on resolving high and medium severity issues can improve the overall reliability of the codebase."
                },
                "codeSecurity": {
                    "figureCaption": "Security Vulnerabilities",
                    "text": "The security analysis indicates that the repositories generally maintain a high-security standard with most receiving an 'A' grade. However, the flickit-platform repository reported a single security issue, highlighting the need for continuous security assessments and updates to mitigate potential vulnerabilities."
                },
                "codeSmell": {
                    "figureCaption": "Code Smells Distribution",
                    "text": "The total count of code smells across all repositories is significant, with the flickit-platform repository having the highest count of 887. Addressing code smells is crucial for improving maintainability and reducing technical debt, which currently stands at 8 days and 12 hours across all repositories."
                },
                "overallCodeQuality": {
                    "text": "The overall code quality assessment reveals strengths and areas for improvement. While maintainability and security grades are generally high, reliability grades vary, with flickit-platform needing attention. Technical debt and code smells are areas where focused improvements can significantly enhance code quality."
                },
                "programmingLanguages": {
                    "info": "A diverse range of programming languages is used across the repositories, including Java, TypeScript, JSON, XML, and more.",
                    "usage": "Java and TypeScript dominate the codebase, accounting for the majority of lines of code. Other languages like Python, XML, and JSON also contribute significantly, reflecting a multi-faceted technology stack."
                },
                "thirdPartyLibraries": {
                    "text": "Third-party library analysis requires external tools such as OWASP Dependency-Check to identify critical updates and known vulnerabilities. Ensuring up-to-date and secure third-party dependencies is critical for maintaining overall project security and stability."
                }
            }
        """;

    private final static String INCORRECT_AI_ANALYSIS = """
            {
                "automatedTestCoverage": {
                    "figureCaption": "Test Coverage Distribution",
                    "text": "The overall test coverage across the repositories shows variability, with some repositories having significant coverage percentages while others lack coverage entirely. The average weighted test coverage is approximately 23.51%, indicating areas for improvement in automated testing practices."
                }
        """;

    private final static String ASSESSOR_ANALYSIS = AI_ANALYSIS;


    public static AssessmentAnalysis assessmentAnalysis() {
        return new AssessmentAnalysis(
            UUID.randomUUID(),
            UUID.randomUUID(),
            AnalysisType.CODE_QUALITY,
            "Ai analysis",
            "Assessor analysis",
            LocalDateTime.now(),
            LocalDateTime.now(),
            "input/path"
        );
    }

    public static AssessmentAnalysis assessmentAnalysisWithAiAnalysis() {
        return new AssessmentAnalysis(
            UUID.randomUUID(),
            UUID.randomUUID(),
            AnalysisType.CODE_QUALITY,
            AI_ANALYSIS,
            null,
            LocalDateTime.now(),
            null,
            "input/path"
        );
    }

    public static AssessmentAnalysis assessmentAnalysisWithAssessorAnalysis() {
        return new AssessmentAnalysis(
            UUID.randomUUID(),
            UUID.randomUUID(),
            AnalysisType.CODE_QUALITY,
            null,
            ASSESSOR_ANALYSIS,
            null,
            LocalDateTime.now(),
            "input/path"
        );
    }

    public static AssessmentAnalysis assessmentAnalysisWithInCorrectAiAnalysis() {
        return new AssessmentAnalysis(
            UUID.randomUUID(),
            UUID.randomUUID(),
            AnalysisType.CODE_QUALITY,
            INCORRECT_AI_ANALYSIS,
            null,
            LocalDateTime.now(),
            null,
            "input/path"
        );
    }
}
