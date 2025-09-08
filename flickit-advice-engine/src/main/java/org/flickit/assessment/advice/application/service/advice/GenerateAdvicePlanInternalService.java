package org.flickit.assessment.advice.application.service.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.advice.application.port.in.advice.GenerateAdvicePlanInternalUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GenerateAdvicePlanInternalService implements GenerateAdvicePlanInternalUseCase {

    private final GenerateAdvicePlanHelper generateAdvicePlanHelper;

    @Override
    public Result generate(Param param) {
        var advices = generateAdvicePlanHelper.createAdvice(param.getAssessmentId(), param.getAttributeLevelTargets());
        return new Result(advices);
    }
}
