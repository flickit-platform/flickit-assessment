package org.flickit.assessment.core.application.domain.report;

public record QuestionnaireReportItem(long id,
                                      String title,
                                      String description,
                                      int index,
                                      int questionCount) {
}
