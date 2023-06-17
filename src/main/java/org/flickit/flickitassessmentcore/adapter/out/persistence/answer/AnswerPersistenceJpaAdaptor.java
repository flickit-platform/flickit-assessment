package org.flickit.flickitassessmentcore.adapter.out.persistence.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.answer.SaveAnswerPort;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
@RequiredArgsConstructor
public class AnswerPersistenceJpaAdaptor implements SaveAnswerPort {

    private final AnswerJpaRepository repository;

    @Override
    public UUID persist(Param param) {
        AnswerJpaEntity unsavedEntity = AnswerMapper.mapSaveParamToJpaEntity(param);
        AnswerJpaEntity entity = repository.save(unsavedEntity);
        return entity.getId();
    }
}
