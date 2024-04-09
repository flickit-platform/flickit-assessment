package org.flickit.assessment.kit.adapter.in.rest.kitdsl;

import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDetailUseCase.KitDetailMaturityLevel;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDetailUseCase.KitDetailQuestionnaire;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDetailUseCase.KitDetailSubject;

import java.util.List;

public record GetKitDetailResponseDto(
    List<KitDetailMaturityLevel> maturityLevels,
    List<KitDetailSubject> subjects,
    List<KitDetailQuestionnaire> questionnaires) {
}
