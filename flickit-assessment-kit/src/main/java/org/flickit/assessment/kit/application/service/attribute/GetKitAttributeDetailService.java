package org.flickit.assessment.kit.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.attribute.GetKitAttributeDetailUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadActiveKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.attribute.CountAttributeImpactfulQuestionsPort;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttributePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadAttributeMaturityLevelsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitAttributeDetailService implements GetKitAttributeDetailUseCase {

    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadAttributePort loadAttributePort;
    private final CountAttributeImpactfulQuestionsPort countAttributeImpactfulQuestionsPort;
    private final LoadAttributeMaturityLevelsPort loadAttributeMaturityLevelsPort;
    private final LoadActiveKitVersionIdPort loadActiveKitVersionIdPort;

    @Override
    public Result getKitAttributeDetail(Param param) {
        var expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId());
        if (!checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        var kitVersionId = loadActiveKitVersionIdPort.loadKitVersionId(param.getKitId());

        var attribute = loadAttributePort.load(param.getAttributeId(), kitVersionId);
        var questionCount = countAttributeImpactfulQuestionsPort.countQuestions(param.getAttributeId(), kitVersionId);
        var maturityLevels = loadAttributeMaturityLevelsPort.loadAttributeLevels(param.getAttributeId(), kitVersionId).stream()
            .map(e -> new GetKitAttributeDetailUseCase.MaturityLevel(e.id(), e.index(), e.title(), e.questionCount()))
            .toList();

        return new Result(
            attribute.getId(),
            attribute.getIndex(),
            attribute.getTitle(),
            questionCount,
            attribute.getWeight(),
            attribute.getDescription(),
            maturityLevels
        );
    }
}
