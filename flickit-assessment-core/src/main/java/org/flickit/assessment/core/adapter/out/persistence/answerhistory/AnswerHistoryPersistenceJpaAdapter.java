package org.flickit.assessment.core.adapter.out.persistence.answerhistory;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.adapter.out.persistence.user.UserMapper;
import org.flickit.assessment.core.application.domain.AnswerHistory;
import org.flickit.assessment.core.application.domain.AnswerStatus;
import org.flickit.assessment.core.application.port.out.answerhistory.CreateAnswerHistoryPort;
import org.flickit.assessment.core.application.port.out.answerhistory.LoadAnswerHistoryPort;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaRepository;
import org.flickit.assessment.data.jpa.core.answerhistory.AnswerHistoryJpaEntity;
import org.flickit.assessment.data.jpa.core.answerhistory.AnswerHistoryJpaRepository;
import org.flickit.assessment.data.jpa.core.answerhistory.QuestionIdAndAnswerCountView;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.flickit.assessment.data.jpa.users.user.UserJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ANSWER_HISTORY_LIST_ASSESSMENT_RESULT_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AnswerHistoryPersistenceJpaAdapter implements
    CreateAnswerHistoryPort,
    LoadAnswerHistoryPort {

    private final AnswerHistoryJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final AnswerJpaRepository answerRepository;
    private final UserJpaRepository userRepository;

    @Override
    public UUID persist(AnswerHistory answerHistory) {
        Long answerOptionId = null;
        Integer answerOptionIndex = null;
        if (answerHistory.getAnswer().getSelectedOption() != null) {
            answerOptionId = answerHistory.getAnswer().getSelectedOption().getId();
            answerOptionIndex = answerHistory.getAnswer().getSelectedOption().getIndex();
        }

        var entity = new AnswerHistoryJpaEntity(
            null,
            new AnswerJpaEntity(answerHistory.getAnswer().getId()),
            new AssessmentResultJpaEntity(answerHistory.getAssessmentResultId()),
            answerHistory.getAnswer().getQuestionId(),
            answerOptionId,
            answerHistory.getAnswer().getConfidenceLevelId(),
            answerHistory.getAnswer().getIsNotApplicable(),
            answerHistory.getAnswer().getAnswerStatus() != null
                ? answerHistory.getAnswer().getAnswerStatus().getId()
                : null,
            answerOptionIndex,
            answerHistory.getCreatedBy().getId(),
            answerHistory.getCreationTime(),
            answerHistory.getHistoryType().ordinal()
        );

        AnswerHistoryJpaEntity savedEntity = repository.save(entity);
        return savedEntity.getId();
    }

    @Override
    public void persistAll(List<AnswerHistory> answerHistories, UUID assessmentResultId) {
        var answerHistoryEntities = answerHistories.stream()
            .map(e -> {
                Long answerOptionId = null;
                Integer answerOptionIndex = null;
                if (e.getAnswer().getSelectedOption() != null) {
                    answerOptionId = e.getAnswer().getSelectedOption().getId();
                    answerOptionIndex = e.getAnswer().getSelectedOption().getIndex();
                }

                return new AnswerHistoryJpaEntity(
                    null,
                    new AnswerJpaEntity(e.getAnswer().getId()),
                    new AssessmentResultJpaEntity(e.getAssessmentResultId()),
                    e.getAnswer().getQuestionId(),
                    answerOptionId,
                    e.getAnswer().getConfidenceLevelId(),
                    e.getAnswer().getIsNotApplicable(),
                    e.getAnswer().getAnswerStatus() != null
                        ? e.getAnswer().getAnswerStatus().getId()
                        : null,
                    answerOptionIndex,
                    e.getCreatedBy().getId(),
                    e.getCreationTime(),
                    e.getHistoryType().ordinal()
                );
            })
            .toList();

        repository.saveAll(answerHistoryEntities);
    }

    @Override
    public void persistOnClearAnswers(PersistOnClearAnswersParam param) {
        var answerIdToQuestionId = answerRepository.findByAssessmentResultIdAndQuestionIdIn(param.assessmentResultId(), param.questionIds()).stream()
            .collect(toMap(AnswerJpaEntity::getId, AnswerJpaEntity::getQuestionId));

        var answerHistoryEntities = answerIdToQuestionId.entrySet().stream()
            .map(a ->
                new AnswerHistoryJpaEntity(
                    null,
                    new AnswerJpaEntity(a.getKey()),
                    new AssessmentResultJpaEntity(param.assessmentResultId()),
                    a.getValue(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    param.createdBy(),
                    param.creationTime(),
                    param.type().ordinal()
                )
            ).toList();
        repository.saveAll(answerHistoryEntities);
    }

    @Override
    public PaginatedResponse<LoadAnswerHistoryPort.Result> load(UUID assessmentId, long questionId, int page, int size) {
        var assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_ANSWER_HISTORY_LIST_ASSESSMENT_RESULT_NOT_FOUND));

        var order = Sort.Direction.DESC;
        var sort = AnswerHistoryJpaEntity.Fields.creationTime;
        var pageResult = repository.findAllByAssessmentResultAndQuestionId(assessmentResult, questionId,
            PageRequest.of(page, size, order, sort));

        Set<UUID> userIds = pageResult.getContent().stream()
            .map(AnswerHistoryJpaEntity::getCreatedBy)
            .collect(Collectors.toSet());
        var userIdToUserMap = userRepository.findAllById(userIds).stream()
            .collect(toMap(UserJpaEntity::getId, Function.identity()));

        var items = pageResult.getContent().stream()
            .map(e -> new LoadAnswerHistoryPort.Result(
                e.getAnswerOptionId(),
                e.getAnswerOptionIndex(),
                e.getConfidenceLevelId(),
                e.getIsNotApplicable(),
                e.getStatus() != null ? AnswerStatus.valueOfById(e.getStatus()) : null,
                UserMapper.mapToFullDomain(userIdToUserMap.get(e.getCreatedBy())),
                e.getCreationTime()))
            .toList();

        return new PaginatedResponse<>(items,
            pageResult.getNumber(),
            pageResult.getSize(),
            sort,
            order.name().toLowerCase(),
            (int) pageResult.getTotalElements());
    }

    @Override
    public Map<Long, Integer> countAnswerHistories(UUID assessmentResultId, Long questionnaireId) {
        return repository.countByAssessmentResultIdAndQuestionIdIn(assessmentResultId, questionnaireId).stream()
            .collect(toMap(QuestionIdAndAnswerCountView::getQuestionId, QuestionIdAndAnswerCountView::getAnswerHistoryCount));
    }
}
