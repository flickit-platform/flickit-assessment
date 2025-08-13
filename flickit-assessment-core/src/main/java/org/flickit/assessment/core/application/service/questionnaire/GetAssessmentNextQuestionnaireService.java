package org.flickit.assessment.core.application.service.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentNextQuestionnaireUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.core.application.port.out.questionnaire.LoadQuestionnairesPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_NEXT_QUESTIONNAIRE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_NEXT_QUESTIONNAIRE_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.QUESTIONNAIRE_ID_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentNextQuestionnaireService implements GetAssessmentNextQuestionnaireUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadQuestionnairesPort loadQuestionnairesPort;
    private final LoadQuestionPort loadQuestionPort;

    @Override
    public Result getNextQuestionnaire(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_NEXT_QUESTIONNAIRE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_NEXT_QUESTIONNAIRE_ASSESSMENT_RESULT_NOT_FOUND));

        var questionnairesProgress =
            loadQuestionnairesPort.loadQuestionnairesProgress(assessmentResult.getKitVersionId(), assessmentResult.getId());
        int currentQuestionnaireIndex = questionnairesProgress.stream()
            .collect(Collectors.toMap(LoadQuestionnairesPort.Result::id, Function.identity()))
            .computeIfAbsent(param.getQuestionnaireId(), id -> {
                throw new ResourceNotFoundException(QUESTIONNAIRE_ID_NOT_FOUND);
            })
            .index();

        var unansweredQuestionnaires = questionnairesProgress.stream()
            .filter(q -> q.answerCount() < q.questionCount())
            .toList();

        Optional<Result> after = unansweredQuestionnaires.stream()
            .filter(q -> q.index() > currentQuestionnaireIndex)
            .min(Comparator.comparingInt(LoadQuestionnairesPort.Result::index))
            .map(q -> new Result.Found(q.id(),
                q.index(),
                q.title(),
                loadQuestionPort.loadFirstUnansweredQuestionIndex(q.id(), assessmentResult.getId())));

        return after.orElseGet(() -> unansweredQuestionnaires.stream()
            .filter(q -> q.index() <= currentQuestionnaireIndex)
            .min(Comparator.comparingInt(LoadQuestionnairesPort.Result::index))
            .<Result>map(q -> new Result.Found(q.id(),
                q.index(),
                q.title(),
                loadQuestionPort.loadFirstUnansweredQuestionIndex(q.id(), assessmentResult.getId())))
            .orElse(Result.NotFound.INSTANCE));
    }
}
