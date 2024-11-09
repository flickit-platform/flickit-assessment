package org.flickit.assessment.kit.application.service.answerrange;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.answerrange.CreateAnswerRangeUseCase;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAnswerRangeService implements CreateAnswerRangeUseCase {

    @Override
    public long createAnswerRange(Param param) {
        return 0;
    }
}
