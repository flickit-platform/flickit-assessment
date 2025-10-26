package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.port.in.assessment.MigrateAssessmentResultKitVersionUseCase;
import org.flickit.assessment.core.application.port.out.answer.DeleteAnswerPort;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.InvalidateAssessmentResultCalculatePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.MIGRATE_KIT_VERSION;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
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
    private final LoadAnswerPort loadAnswerPort;
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

        var currentKitVersionQuestionsIds = loadQuestionPort.loadIdsByKitVersionId(assessmentResult.getKitVersionId());
        var activeKitVersionQuestionsIds = loadQuestionPort.loadIdsByKitVersionId(activeKitVersionId);
        var missingQuestionIds = currentKitVersionQuestionsIds.stream().
            filter(q -> !activeKitVersionQuestionsIds.contains(q))
            .collect(Collectors.toSet());

        if (!missingQuestionIds.isEmpty())
            deleteAnswerPort.delete(assessmentResult.getId(), missingQuestionIds);

        var currentQuestions = loadQuestionPort.loadByKitVersionId(assessmentResult.getKitVersionId());
        var activeQuestions = loadQuestionPort.loadByKitVersionId(activeKitVersionId);
        var questionIdsWithChangedAnswerRange = findChangedAnswerRanges(currentQuestions, activeQuestions);

        if (!questionIdsWithChangedAnswerRange.isEmpty()) {
            var answerIdsOfQuestionsWithMissingAnswerRange = loadAnswerPort.loadIdsByQuestionIds(questionIdsWithChangedAnswerRange);
            if (!answerIdsOfQuestionsWithMissingAnswerRange.isEmpty())
                deleteAnswerPort.deleteSelectedOption(answerIdsOfQuestionsWithMissingAnswerRange, loadUserPort.loadSystemUserId());
        }

        updateAssessmentResultPort.updateKitVersionId(assessmentResult.getId(), activeKitVersionId);
        loadAssessmentResultCalculatePort.invalidateCalculate(assessmentResult.getId());
    }


    public static List<Long> findChangedAnswerRanges(List<LoadQuestionPort.Result> currentQuestions, List<LoadQuestionPort.Result> activeQuestions) {
        Map<Long, Long> currentQuestionIdToAnswerRageIdMap = currentQuestions.stream()
            .collect(Collectors.toMap(LoadQuestionPort.Result::id, LoadQuestionPort.Result::answerRangeId));

        return activeQuestions.stream()
            .filter(q -> currentQuestionIdToAnswerRageIdMap.containsKey(q.id()))
            .filter(q -> !Objects.equals(currentQuestionIdToAnswerRageIdMap.get(q.id()), q.answerRangeId()))
            .map(LoadQuestionPort.Result::id)
            .toList();
    }
}
