package org.flickit.assessment.core.application.port.out.attribute;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.util.UUID;

public interface LoadAttributeScoreDetailPort {

    PaginatedResponse<Result> loadScoreDetail(Param param);

    record Param(UUID assessmentId,
                 long attributeId,
                 long maturityLevelId,
                 String sort,
                 String order,
                 int size,
                 int page) {}

    record Result(String questionnaireTitle,
                  String questionTitle,
                  int index,
                  String answer,
                  Boolean answerIsNotApplicable,
                  int questionWeight,
                  Double answerScore,
                  double weightedScore,
                  Integer confidence) {
    }
}
