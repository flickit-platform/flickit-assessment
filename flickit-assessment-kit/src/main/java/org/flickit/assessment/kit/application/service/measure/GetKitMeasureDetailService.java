package org.flickit.assessment.kit.application.service.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.port.in.measure.GetKitMeasureDetailUseCase;
import org.flickit.assessment.kit.application.port.out.answerrange.LoadAnswerRangesPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadActiveKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.measure.LoadMeasurePort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionsPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairesPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_MEASURE_DETAIL_MEASURE_ID_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitMeasureDetailService implements GetKitMeasureDetailUseCase {

    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadActiveKitVersionIdPort loadActiveKitVersionIdPort;
    private final LoadMeasurePort loadMeasurePort;
    private final LoadQuestionsPort loadQuestionsPort;
    private final LoadQuestionnairesPort loadQuestionnairesPort;
    private final LoadAnswerRangesPort loadAnswerRangesPort;

    @Override
    public Result getKitMeasureDetail(Param param) {
        ExpertGroup expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId());
        if (!checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var kitVersionId = loadActiveKitVersionIdPort.loadKitVersionId(param.getKitId());
        var measure = loadMeasurePort.load(param.getMeasureId(), kitVersionId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_KIT_MEASURE_DETAIL_MEASURE_ID_NOT_FOUND));
        var questions = loadQuestionsPort.loadAllByMeasureIdAndKitVersionId(measure.getId(), kitVersionId);

        return new Result(measure.getTitle(),
            measure.getDescription(),
            questions.size(),
            toKitMeasureQuestions(param.getKitId(), kitVersionId, questions),
            measure.getTranslations());
    }

    private List<MeasureDetailQuestion> toKitMeasureQuestions(long kitId, long kitVersionId, List<Question> questions) {
        var questionnaireIdToQuestionnaireMap = loadQuestionnairesPort.loadByKitId(kitId).stream()
            .collect(Collectors.toMap(Questionnaire::getId, Function.identity()));
        var answerRangeIdToAnswerRangeMap = loadAnswerRangesPort.loadAll(kitVersionId).stream()
            .collect(Collectors.toMap(AnswerRange::getId, Function.identity()));

        return questions.stream()
            .sorted(Comparator.comparingInt(Question::getIndex))
            .map(question -> new MeasureDetailQuestion(question.getTitle(),
                MeasureDetailAnswerRange.of(answerRangeIdToAnswerRangeMap.get(question.getAnswerRangeId())),
                MeasureDetailQuestionnaire.of(questionnaireIdToQuestionnaireMap.get(question.getQuestionnaireId())),
                answerRangeIdToAnswerRangeMap.get(question.getAnswerRangeId()).getAnswerOptions().stream()
                    .map(MeasureDetailAnswerOption::of)
                    .toList())
            )
            .toList();
    }
}
