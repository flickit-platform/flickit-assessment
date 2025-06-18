package org.flickit.assessment.core.application.service.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentNextQuestionnaireUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.questionnaire.LoadQuestionnairesPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentNextQuestionnaireService implements GetAssessmentNextQuestionnaireUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadQuestionnairesPort loadQuestionnairesPort;

    @Override
    public Result getNextQuestionnaire(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_NEXT_QUESTIONNAIRE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_NEXT_QUESTIONNAIRE_ASSESSMENT_RESULT_NOT_FOUND));

        var questionnaireIdToQuestionnaireDetails =
            loadQuestionnairesPort.loadQuestionnaireDetails(assessmentResult.getKitVersionId(), assessmentResult.getId());

        var currentQuestionnaire = Optional.ofNullable(
            questionnaireIdToQuestionnaireDetails.get(param.getQuestionnaireId())
        ).orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_NEXT_QUESTIONNAIRE_QUESTIONNAIRE_NOT_FOUND));
        int currentIndex = currentQuestionnaire.index();

        Predicate<LoadQuestionnairesPort.Result> isUnansweredAndAfter = q -> q.index() > currentIndex && q.answerCount() < q.questionCount();
        return questionnaireIdToQuestionnaireDetails.values().stream()
            .filter(isUnansweredAndAfter)
            .min(Comparator.comparingInt(LoadQuestionnairesPort.Result::index))
            .map(e -> new Result(e.id(), e.index(), e.title()))
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_NEXT_QUESTIONNAIRE_NEXT_QUESTIONNAIRE_NOT_FOUND));
    }
}
