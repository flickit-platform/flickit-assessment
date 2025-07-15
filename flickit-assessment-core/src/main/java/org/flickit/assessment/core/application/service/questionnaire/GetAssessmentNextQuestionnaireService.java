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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

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

    @Override
    public Result getNextQuestionnaire(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_NEXT_QUESTIONNAIRE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_NEXT_QUESTIONNAIRE_ASSESSMENT_RESULT_NOT_FOUND));

        var questionnaireIdToDetailMap =
            loadQuestionnairesPort.loadQuestionnaireDetails(assessmentResult.getKitVersionId(), assessmentResult.getId());

        Optional.ofNullable(questionnaireIdToDetailMap.get(param.getQuestionnaireId()))
            .orElseThrow(() -> new ResourceNotFoundException(QUESTIONNAIRE_ID_NOT_FOUND));

        var allQuestionnaireDetails = new ArrayList<>(questionnaireIdToDetailMap.values());

        Predicate<LoadQuestionnairesPort.Result> isUnanswered = q -> q.answerCount() < q.questionCount();

        int currentIndex = questionnaireIdToDetailMap.get(param.getQuestionnaireId()).index();
        Optional<Result> after = allQuestionnaireDetails.stream()
            .filter(q -> q.index() > currentIndex && isUnanswered.test(q))
            .min(Comparator.comparingInt(LoadQuestionnairesPort.Result::index))
            .map(q -> new Result.Found(q.id(), q.index(), q.title()));

        return after.orElseGet(() -> allQuestionnaireDetails.stream()
            .filter(q -> q.index() <= currentIndex && isUnanswered.test(q))
            .min(Comparator.comparingInt(LoadQuestionnairesPort.Result::index))
            .<Result>map(q -> new Result.Found(q.id(), q.index(), q.title()))
            .orElse(Result.NotFound.INSTANCE));
    }
}
