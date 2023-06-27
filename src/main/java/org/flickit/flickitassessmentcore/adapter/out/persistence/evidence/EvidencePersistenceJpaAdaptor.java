package org.flickit.flickitassessmentcore.adapter.out.persistence.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.evidence.LoadEvidencesByQuestionPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EvidencePersistenceJpaAdaptor implements LoadEvidencesByQuestionPort {

    private final EvidenceJpaRepository repository;

    @Override
    public Result loadEvidencesByQuestionId(Param param, int page, int size) {
        return new Result(repository.findByQuestionIdOrderByLastModificationDateDesc(param.questionId(), PageRequest.of(page, size)).stream()
            .map(EvidenceMapper::toDomainModel)
            .collect(Collectors.toList()));
    }
}
