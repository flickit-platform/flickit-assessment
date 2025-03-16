package org.flickit.assessment.core.adapter.out.persistence.answerhistory;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AnswerHistory;
import org.flickit.assessment.core.application.port.out.answerhistory.CreateAnswerHistoryPort;
import org.flickit.assessment.core.application.port.out.answerhistory.LoadAnswerHistoryListPort;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaRepository;
import org.flickit.assessment.data.jpa.core.answerhistory.AnswerHistoryJpaEntity;
import org.flickit.assessment.data.jpa.core.answerhistory.AnswerHistoryJpaRepository;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.flickit.assessment.data.jpa.users.user.UserJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.adapter.out.persistence.answerhistory.AnswerHistoryMapper.mapCreateParamToJpaEntity;
import static org.flickit.assessment.core.adapter.out.persistence.answerhistory.AnswerHistoryMapper.mapToDomainModel;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Component
@RequiredArgsConstructor
public class AnswerHistoryPersistenceJpaAdapter implements
    CreateAnswerHistoryPort,
    LoadAnswerHistoryListPort {

    private final AnswerHistoryJpaRepository repository;
    private final AnswerJpaRepository answerRepository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final UserJpaRepository userRepository;
    private final AnswerOptionJpaRepository answerOptionRepository;

    @Override
    public UUID persist(AnswerHistory answerHistory) {
        var assessmentResult = assessmentResultRepository.findById(answerHistory.getAssessmentResultId())
            .orElseThrow(() -> new ResourceNotFoundException(SUBMIT_ANSWER_ASSESSMENT_RESULT_NOT_FOUND));
        var answer = answerRepository.findById(answerHistory.getAnswer().getId())
            .orElseThrow(() -> new ResourceNotFoundException(SUBMIT_ANSWER_ANSWER_ID_NOT_FOUND));

        AnswerHistoryJpaEntity savedEntity = repository.save(mapCreateParamToJpaEntity(answerHistory, assessmentResult, answer));
        return savedEntity.getId();
    }

    @Override
    public void persistAll(List<AnswerHistory> answerHistories, UUID assessmentResultId) {
        var assessmentResult = assessmentResultRepository.findById(assessmentResultId)
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));

        var answerIds = answerHistories.stream()
            .map(history -> history.getAnswer().getId())
            .toList();

        var answerIdToEntityMap = answerRepository.findAllById(answerIds).stream()
            .collect(toMap(AnswerJpaEntity::getId, Function.identity()));

        var answerHistoryEntities = answerHistories.stream()
            .map(e -> mapCreateParamToJpaEntity(e, assessmentResult, answerIdToEntityMap.get(e.getAnswer().getId())))
            .toList();

        repository.saveAll(answerHistoryEntities);
    }

    @Override
    public PaginatedResponse<AnswerHistory> load(UUID assessmentId, long questionId, int page, int size) {
        var assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_ANSWER_HISTORY_LIST_ASSESSMENT_RESULT_NOT_FOUND));

        var sort = Sort.Direction.DESC;
        var order = AnswerHistoryJpaEntity.Fields.creationTime;
        var pageResult = repository.findAllByAssessmentResultAndQuestionId(assessmentResult, questionId,
            PageRequest.of(page, size, sort, order));

        Set<UUID> userIds = pageResult.getContent().stream()
            .map(AnswerHistoryJpaEntity::getCreatedBy)
            .collect(Collectors.toSet());
        var userIdToUserMap = userRepository.findAllById(userIds).stream()
            .collect(toMap(UserJpaEntity::getId, Function.identity()));

        var idToOption = answerOptionRepository.findByQuestionIdAndKitVersionId(questionId, assessmentResult.getKitVersionId()).stream()
            .collect(toMap(AnswerOptionJpaEntity::getId, Function.identity()));

        var items = pageResult.getContent().stream()
            .map(e -> mapToDomainModel(e, userIdToUserMap.get(e.getCreatedBy()), idToOption.get(e.getAnswerOptionId())))
            .toList();

        return new PaginatedResponse<>(items,
            pageResult.getNumber(),
            pageResult.getSize(),
            order,
            sort.name().toLowerCase(),
            (int) pageResult.getTotalElements());
    }
}
