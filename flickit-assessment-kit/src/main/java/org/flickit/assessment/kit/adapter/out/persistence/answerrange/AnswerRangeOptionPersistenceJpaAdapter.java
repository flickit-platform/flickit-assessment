package org.flickit.assessment.kit.adapter.out.persistence.answerrange;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.flickit.assessment.data.jpa.kit.answerrange.AnswerRangeJpaEntity;
import org.flickit.assessment.data.jpa.kit.answerrange.AnswerRangeJpaRepository;
import org.flickit.assessment.kit.adapter.out.persistence.answeroption.AnswerOptionMapper;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.domain.AnswerRange;
import org.flickit.assessment.kit.application.port.out.answerange.LoadAnswerRangePort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Component
@RequiredArgsConstructor
public class AnswerRangeOptionPersistenceJpaAdapter implements LoadAnswerRangePort {

    private final AnswerRangeJpaRepository repository;
    private final AnswerOptionJpaRepository answerOptionJpaRepository;

    @Override
    public PaginatedResponse<AnswerRange> loadByKitVersionId(long kitVersionId, int page, int size) {
        Page<AnswerRangeJpaEntity> pageResult = repository.findReusableByKitVersionId(kitVersionId, PageRequest.of(page, size));
        var answerRanges = pageResult
            .getContent()
            .stream()
            .map(AnswerRangeMapper::toDomainModel)
            .toList();

        var answerOptions = answerOptionJpaRepository.findAllByKitVersionId(kitVersionId)
            .stream().map(AnswerOptionMapper::mapToDomainModel)
            .toList();

        return new PaginatedResponse<>(
            getAnswerRangesWithAnswerOptions(answerRanges, answerOptions),
            pageResult.getNumber(),
            pageResult.getSize(),
            AnswerRangeJpaEntity.Fields.creationTime,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    private static List<AnswerRange> getAnswerRangesWithAnswerOptions(List<AnswerRange> answerRanges, List<AnswerOption> answerOptions) {
        Map<Long, List<AnswerOption>> answerRangeIdToOptions = answerOptions.stream()
            .collect(groupingBy(AnswerOption::getAnswerRangeId));

        return answerRanges.stream()
            .map(answerRange -> new AnswerRange(
                answerRange.getId(),
                answerRange.getTitle(),
                answerRangeIdToOptions.getOrDefault(answerRange.getId(), List.of()).stream()
                    .map(option -> new AnswerOption(
                        option.getId(),
                        option.getTitle(),
                        option.getIndex(),
                        null,
                        null
                    )).toList()
            )).toList();
    }
}
