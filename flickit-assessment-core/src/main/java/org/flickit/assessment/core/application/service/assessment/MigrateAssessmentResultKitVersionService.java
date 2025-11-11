package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.port.in.assessment.MigrateAssessmentResultKitVersionUseCase;
import org.flickit.assessment.core.application.port.out.answer.DeleteAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.UpdateAnswerPort;
import org.flickit.assessment.core.application.port.out.answerhistory.CreateAnswerHistoryPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.InvalidateAssessmentResultCalculatePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionPort.IdAndAnswerRange;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.MIGRATE_KIT_VERSION;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.application.domain.HistoryType.DELETE;
import static org.flickit.assessment.core.common.ErrorMessageKey.MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ACTIVE_VERSION_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ASSESSMENT_RESULT_ID_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class MigrateAssessmentResultKitVersionService implements MigrateAssessmentResultKitVersionUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final InvalidateAssessmentResultCalculatePort loadAssessmentResultCalculatePort;
    private final UpdateAssessmentResultPort updateAssessmentResultPort;
    private final LoadQuestionPort loadQuestionPort;
    private final DeleteAnswerPort deleteAnswerPort;
    private final CreateAnswerHistoryPort createAnswerHistoryPort;
    private final UpdateAnswerPort updateAnswerPort;
    private final LoadUserPort loadUserPort;

    @Override
    public void migrateKitVersion(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MIGRATE_KIT_VERSION))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ASSESSMENT_RESULT_ID_NOT_FOUND));

        var activeKitVersionId = assessmentResult.getAssessment().getAssessmentKit().getKitVersion();
        if (activeKitVersionId == null)
            throw new ValidationException(MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ACTIVE_VERSION_NOT_FOUND);

        List<IdAndAnswerRange> currentQuestions = loadQuestionPort.loadIdAndAnswerRangeIdByKitVersionId(assessmentResult.getKitVersionId());
        List<IdAndAnswerRange> activeVersionQuestions = loadQuestionPort.loadIdAndAnswerRangeIdByKitVersionId(activeKitVersionId);

        Map<Long, Long> activeQuestionsMap = activeVersionQuestions.stream()
            .collect(Collectors.toMap(IdAndAnswerRange::id, IdAndAnswerRange::answerRangeId));

        deleteAnswersOfDeletedQuestions(currentQuestions, activeQuestionsMap, assessmentResult.getId());
        clearAnswersOfChangedQuestions(currentQuestions, activeQuestionsMap, assessmentResult.getId());

        updateAssessmentResultPort.updateKitVersionId(assessmentResult.getId(), activeKitVersionId);
        loadAssessmentResultCalculatePort.invalidateCalculate(assessmentResult.getId());
    }

    private void deleteAnswersOfDeletedQuestions(List<IdAndAnswerRange> currentQuestions,
                                                 Map<Long, Long> activeQuestionsMap,
                                                 UUID assessmentResultId) {
        Set<Long> missingQuestionsInActiveVersion = currentQuestions.stream()
            .map(IdAndAnswerRange::id)
            .filter(id -> !activeQuestionsMap.containsKey(id))
            .collect(toSet());

        if (!missingQuestionsInActiveVersion.isEmpty())
            deleteAnswerPort.delete(assessmentResultId, missingQuestionsInActiveVersion);
    }

    private void clearAnswersOfChangedQuestions(List<IdAndAnswerRange> currentQuestions,
                                                Map<Long, Long> activeQuestionsMap,
                                                UUID assessmentResultId) {
        List<Long> questionIdsWithChangedAnswerRange = currentQuestions.stream()
            .filter(q -> {
                Long activeAnswerRangeId = activeQuestionsMap.get(q.id());
                return activeAnswerRangeId != null && activeAnswerRangeId != q.answerRangeId();
            })
            .map(IdAndAnswerRange::id)
            .toList();

        if (!questionIdsWithChangedAnswerRange.isEmpty()) {
            var systemUserId = loadUserPort.loadSystemUserId();
            updateAnswerPort.clearAnswers(assessmentResultId, questionIdsWithChangedAnswerRange, systemUserId);

            var persistOnClearAnswersParam = new CreateAnswerHistoryPort.PersistOnClearAnswersParam(assessmentResultId,
                questionIdsWithChangedAnswerRange,
                systemUserId,
                LocalDateTime.now(),
                DELETE);
            createAnswerHistoryPort.persistOnClearAnswers(persistOnClearAnswersParam);
        }
    }
}
