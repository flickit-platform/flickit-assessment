package org.flickit.assessment.advice.adapter.out.calculation;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.adapter.out.persistence.answeroption.AnswerOptionMapper;
import org.flickit.assessment.advice.adapter.out.persistence.attribute.AttributeMapper;
import org.flickit.assessment.advice.adapter.out.persistence.question.QuestionMapper;
import org.flickit.assessment.advice.adapter.out.persistence.questionnaire.QuestionnaireMapper;
import org.flickit.assessment.advice.application.port.out.calculation.LoadCreatedAdviceDetailsPort;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.error.ErrorMessageKey;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.question.QuestionAdviceView;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.groupingBy;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class LoadCreatedAdviceDetailsAdapter implements
    LoadCreatedAdviceDetailsPort {

    private final QuestionJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final AssessmentKitJpaRepository assessmentKitRepository;

    @Override
    public List<Result> loadAdviceDetails(List<Long> questionIds, UUID assessmentId) {
        var assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        var questionViews = repository.findAdviceQuestionsDetail(questionIds, assessmentResult.getKitVersionId());

        var translationLanguage = resolveLanguage(assessmentResult);
        return questionViews.stream()
            .collect(groupingBy(QuestionAdviceView::getQuestion))
            .values().stream()
            .map(views -> mapToAdviceListItem(views, translationLanguage))
            .toList();
    }

    private KitLanguage resolveLanguage(AssessmentResultJpaEntity assessmentResult) {
        var assessmentKit = assessmentKitRepository.findByKitVersionId(assessmentResult.getKitVersionId())
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessageKey.COMMON_ASSESSMENT_KIT_NOT_FOUND));
        return Objects.equals(assessmentResult.getLangId(), assessmentKit.getLanguageId())
            ? null
            : KitLanguage.valueOfById(assessmentResult.getLangId());
    }

    private LoadCreatedAdviceDetailsPort.Result mapToAdviceListItem(List<QuestionAdviceView> views, KitLanguage language) {
        var view = views.getFirst();
        var question = QuestionMapper.toAdviceItem(view.getQuestion(), language);

        var options = views.stream()
            .map(QuestionAdviceView::getOption)
            .filter(distinctByKey(AnswerOptionJpaEntity::getIndex))
            .map(entity -> AnswerOptionMapper.toAdviceItem(entity, language))
            .toList();

        var attributes = views.stream()
            .map(QuestionAdviceView::getAttribute)
            .filter(distinctByKey(AttributeJpaEntity::getId))
            .map(entity -> AttributeMapper.toAdviceItem(entity, language))
            .toList();

        var questionnaire = QuestionnaireMapper.toAdviceItem(view.getQuestionnaire(), language);

        return new LoadCreatedAdviceDetailsPort.Result(
            question,
            options,
            attributes,
            questionnaire
        );
    }

    private <T> Predicate<T> distinctByKey(
        Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
