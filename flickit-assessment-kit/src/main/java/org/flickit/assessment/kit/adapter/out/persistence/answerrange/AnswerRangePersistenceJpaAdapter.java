package org.flickit.assessment.kit.adapter.out.persistence.answerrange;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.flickit.assessment.data.jpa.kit.answerrange.AnswerRangeJpaEntity;
import org.flickit.assessment.data.jpa.kit.answerrange.AnswerRangeJpaRepository;
import org.flickit.assessment.data.jpa.kit.seq.KitDbSequenceGenerators;
import org.flickit.assessment.kit.adapter.out.persistence.answeroption.AnswerOptionMapper;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.domain.AnswerRange;
import org.flickit.assessment.kit.application.port.out.answerange.LoadAnswerRangesPort;
import org.flickit.assessment.kit.application.port.out.answerrange.CreateAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.answerrange.UpdateAnswerRangePort;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.flickit.assessment.kit.application.port.out.answerrange.UpdateAnswerRangePort;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.kit.common.ErrorMessageKey.ANSWER_RANGE_ID_NOT_FOUND;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.adapter.out.persistence.answerrange.AnswerRangeMapper.toDomainModel;

@Component
@RequiredArgsConstructor
public class AnswerRangePersistenceJpaAdapter implements
    CreateAnswerRangePort,
    UpdateAnswerRangePort,
    LoadAnswerRangesPort {

    private final AnswerRangeJpaRepository repository;
    private final AnswerOptionJpaRepository answerOptionRepository;
    private final KitDbSequenceGenerators sequenceGenerators;

    @Override
    public long persist(CreateAnswerRangePort.Param param) {
        var entity = AnswerRangeMapper.toJpaEntity(param);
        entity.setId(sequenceGenerators.generateAnswerRangeId());
        return repository.save(entity).getId();
    }

    @Override
    public PaginatedResponse<AnswerRange> loadByKitVersionId(long kitVersionId, int page, int size) {
        var order = AnswerRangeJpaEntity.Fields.creationTime;
        var sort = Sort.Direction.ASC;
        var pageResult = repository.findByKitVersionIdAndReusableTrue(kitVersionId, PageRequest.of(page, size, sort, order));
        List<Long> answerRangeEntityIds = pageResult.getContent().stream().map(AnswerRangeJpaEntity::getId).toList();
        var answerOptionEntities = answerOptionRepository.findAllByAnswerRangeIdInAndKitVersionId(answerRangeEntityIds, kitVersionId);

        return new PaginatedResponse<>(
            getAnswerRanges(pageResult.getContent(), answerOptionEntities),
            pageResult.getNumber(),
            pageResult.getSize(),
            order,
            sort.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    private @NotNull List<AnswerRange> getAnswerRanges(List<AnswerRangeJpaEntity> answerRangeEntities, List<AnswerOptionJpaEntity> answerOptionEntities) {
        Map<Long, List<AnswerOptionJpaEntity>> answerRangeIdToAnswerOptionsMap = answerOptionEntities.stream()
            .collect(Collectors.groupingBy(
                AnswerOptionJpaEntity::getAnswerRangeId,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> list.stream()
                        .sorted(Comparator.comparingInt(AnswerOptionJpaEntity::getIndex))
                        .toList())
            ));

        return answerRangeEntities.stream().map(entity -> {
            List<AnswerOption> options = answerRangeIdToAnswerOptionsMap.get(entity.getId()).stream()
                .map(AnswerOptionMapper::mapToDomainModel)
                .toList();

            return toDomainModel(entity, options);
        }).toList();
    }

    @Override
    public void update(UpdateAnswerRangePort.Param param) {
        if (!repository.existsByIdAndKitVersionId(param.answerRangeId(), param.kitVersionId()))
            throw new ResourceNotFoundException(ANSWER_RANGE_ID_NOT_FOUND);

        repository.update(param.answerRangeId(),
            param.kitVersionId(),
            param.title(),
            param.reusable(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }
}
