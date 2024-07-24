package org.flickit.assessment.core.adapter.out.persistence.answerhistory;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AnswerHistory;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.port.out.answerhistory.CreateAnswerHistoryPort;
import org.flickit.assessment.core.application.port.out.answerhistory.LoadAnswerHistoryListPort;
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
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.flickit.assessment.core.adapter.out.persistence.answerhistory.AnswerHistoryMapper.mapCreateParamToJpaEntity;
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
    public PaginatedResponse<AnswerHistoryListItem> loadByAssessmentIdAndQuestionId(UUID assessmentId,
                                                                                    long questionId,
                                                                                    int page,
                                                                                    int size) {
        var assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_ANSWER_HISTORY_LIST_ASSESSMENT_RESULT_NOT_FOUND));
        var pageResult = repository.findAllByAssessmentResultAndQuestionId(assessmentResult,
            questionId,
            PageRequest.of(page, size));
        Set<UUID> userIds = pageResult.getContent().stream()
            .map(AnswerHistoryJpaEntity::getModifiedBy)
            .collect(Collectors.toSet());
        Map<UUID, String> userIdToDisplayName = userRepository.findAllById(userIds).stream()
            .collect(Collectors.toMap(UserJpaEntity::getId, UserJpaEntity::getDisplayName));
        Set<Long> answerOptionIds = pageResult.getContent().stream()
            .map(AnswerHistoryJpaEntity::getAnswerOptionId)
            .collect(Collectors.toSet());
        Map<Long, Integer> answerOptionIdToIndex =
            answerOptionRepository.findAllByIdInAndKitVersionId(answerOptionIds, assessmentResult.getKitVersionId()).stream()
                .collect(Collectors.toMap(AnswerOptionJpaEntity::getId, AnswerOptionJpaEntity::getIndex));

        List<AnswerHistoryListItem> items = pageResult.getContent().stream()
            .map(e -> new AnswerHistoryListItem(e.getModifiedAt(),
                userIdToDisplayName.get(e.getModifiedBy()),
                ConfidenceLevel.valueOfById(e.getConfidenceLevelId()).getTitle(),
                answerOptionIdToIndex.get(e.getAnswerOptionId()),
                e.getIsNotApplicable()))
            .toList();

        return new PaginatedResponse<>(items,
            pageResult.getNumber(),
            pageResult.getSize(),
            AnswerHistoryJpaEntity.Feilds.creationTime(),
            Sort.Direction.DESC.name().toLowerCase(),
            (int) pageResult.getTotalElements());
    }
}
