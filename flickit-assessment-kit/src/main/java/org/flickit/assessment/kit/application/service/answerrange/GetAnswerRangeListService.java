package org.flickit.assessment.kit.application.service.answerrange;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.AnswerRange;
import org.flickit.assessment.kit.application.port.in.answerrange.GetAnswerRangeListUseCase;
import org.flickit.assessment.kit.application.port.out.answerange.LoadAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAnswerRangeListService implements GetAnswerRangeListUseCase {

    @Override
    public PaginatedResponse<AnswerRangeListItem> getAnswerRangeList(Param param) {
    public PaginatedResponse<AnswerRange> getAnswerRangeList(Param param) {

        return null;
    }
}
