package org.flickit.assessment.kit.application.service.answerrange;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.answerrange.GetAnswerRangeListUseCase;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAnswerRangeListService implements GetAnswerRangeListUseCase {

    @Override
    public PaginatedResponse<AnswerRangeListItem> getAnswerRangeList(Param param) {

        return null;
    }
}
