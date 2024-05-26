package org.flickit.assessment.core.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageKey {

    public static final String VIEWER_DESCRIPTION = "Can view the assessment reports.";
    public static final String COMMENTER_DESCRIPTION = """
        Can view the assessment reports. Can also comment on the assessment questions.
        """;
    public static final String ASSESSOR_DESCRIPTION = """
        Can view the assessment reports. They can also conduct the assessment process (e.g., answering questions, submitting evidences, etc.)
        """;
    public static final String MANAGER_DESCRIPTION = """
        Can view the assessment reports, conduct the assessment process, and also manage others' access to the assessments.
        """;
}
