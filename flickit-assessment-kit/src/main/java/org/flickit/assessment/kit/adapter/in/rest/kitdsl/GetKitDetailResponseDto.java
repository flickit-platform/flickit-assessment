package org.flickit.assessment.kit.adapter.in.rest.kitdsl;

import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDetailUseCase.*;

import java.util.List;

public record GetKitDetailResponseDto(
    List<KitDetailMaturityLevel> maturityLevels,
    List<KitDetailSubject> subjects,
    List<KitDetailQuestionnaire> questionnaires,
    List<KitDetailMeasure> measures,
    List<KitDetailAnswerRange> answerRanges) {
}
