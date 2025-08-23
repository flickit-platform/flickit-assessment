package org.flickit.assessment.core.application.service.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.measure.GetAttributeMeasureQuestionsUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAttributeMeasureQuestionsService implements GetAttributeMeasureQuestionsUseCase {

    @Override
    public Result getAttributeMeasureQuestions(Param param) {
        return null;
    }
}
