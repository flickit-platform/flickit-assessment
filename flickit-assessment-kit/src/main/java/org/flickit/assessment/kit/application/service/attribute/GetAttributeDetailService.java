package org.flickit.assessment.kit.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.attribute.GetAttributeDetailUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttributePort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadAttributeMaturityLevelPort;
import org.flickit.assessment.kit.application.port.out.question.LoadAttributeQuestionCountPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_ATTRIBUTE_DETAIL_ATTRIBUTE_ID_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAttributeDetailService implements GetAttributeDetailUseCase {

    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadAttributePort loadAttributePort;
    private final LoadAttributeQuestionCountPort loadAttributeQuestionCountPort;
    private final LoadAttributeMaturityLevelPort loadAttributeMaturityLevelPort;

    @Override
    public Result getAttributeDetail(Param param) {
        var expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId());
        if (!checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var attribute = loadAttributePort.loadByIdAndKitId(param.getAttributeId(), param.getKitId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ATTRIBUTE_DETAIL_ATTRIBUTE_ID_NOT_FOUND));
        var questionCount = loadAttributeQuestionCountPort.loadByAttributeId(param.getAttributeId());
        var maturityLevels = loadAttributeMaturityLevelPort.loadByAttributeId(param.getAttributeId());

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
