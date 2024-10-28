package org.flickit.assessment.kit.application.service.answerrange;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AnswerRange;
import org.flickit.assessment.kit.application.port.in.answerrange.GetAnswerRangeListUseCase;
import org.flickit.assessment.kit.application.port.out.answerange.LoadAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.springframework.stereotype.Service;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@RequiredArgsConstructor
public class GetAnswerRangeListService implements GetAnswerRangeListUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadAnswerRangePort loadAnswerRangePort;

    @Override
    public PaginatedResponse<AnswerRange> getAnswerRangeList(Param param) {
        var kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        if (!checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        return null;
    }
}
