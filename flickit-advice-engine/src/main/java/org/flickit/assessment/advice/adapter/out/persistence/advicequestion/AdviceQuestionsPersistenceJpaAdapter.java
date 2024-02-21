package org.flickit.assessment.advice.adapter.out.persistence.advicequestion;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.advice.CreateAdviceUseCase;
import org.flickit.assessment.advice.application.port.out.advicequestion.CreateAdviceQuestionPort;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.advice.advice.AdviceJpaEntity;
import org.flickit.assessment.data.jpa.advice.advice.AdviceJpaRepository;
import org.flickit.assessment.data.jpa.advice.advicequestion.AdviceQuestionJpaEntity;
import org.flickit.assessment.data.jpa.advice.advicequestion.AdviceQuestionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ADVICE_ADVICE_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AdviceQuestionsPersistenceJpaAdapter implements CreateAdviceQuestionPort {

    private final AdviceJpaRepository assessmentAdviceRepository;
    private final AdviceQuestionJpaRepository repository;

    @Override
    public void persistAll(UUID adviceId, List<CreateAdviceUseCase.AdviceQuestion> adviceQuestions) {
        AdviceJpaEntity advice = assessmentAdviceRepository.findById(adviceId)
            .orElseThrow(()->new ResourceNotFoundException(CREATE_ADVICE_ADVICE_NOT_FOUND));
        List<AdviceQuestionJpaEntity> adviceQuestionJpaEntities = adviceQuestions.stream()
            .map(q -> AdviceQuestionMapper.mapToEntity(q, advice))
            .toList();
        repository.saveAll(adviceQuestionJpaEntities);
    }
}
