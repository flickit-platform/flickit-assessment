package org.flickit.assessment.kit.adapter.out.persistence.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.flickit.assessment.data.jpa.kit.answeroption.ImpactfulAnswerOptionView;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.AttrLevelQuestionView;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireTitleView;
import org.flickit.assessment.kit.adapter.out.persistence.answeroption.AnswerOptionMapper;
import org.flickit.assessment.kit.adapter.out.persistence.answeroptionimpact.AnswerOptionImpactMapper;
import org.flickit.assessment.kit.adapter.out.persistence.questionimpact.QuestionImpactMapper;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.domain.QuestionImpact;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttrLevelQuestionsInfoPort;
import org.flickit.assessment.kit.application.port.out.question.CreateQuestionPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.flickit.assessment.kit.application.port.out.subject.CountSubjectQuestionsPort;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.adapter.out.persistence.question.QuestionMapper.mapToJpaEntity;
import static org.flickit.assessment.kit.common.ErrorMessageKey.QUESTION_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class QuestionPersistenceJpaAdapter implements
    UpdateQuestionPort,
    CreateQuestionPort,
    CountSubjectQuestionsPort,
    LoadQuestionPort,
    LoadAttrLevelQuestionsInfoPort {

    private final QuestionJpaRepository repository;
    private final QuestionImpactJpaRepository questionImpactRepository;
    private final AnswerOptionImpactJpaRepository answerOptionImpactRepository;
    private final AnswerOptionJpaRepository answerOptionRepository;
    private final MaturityLevelJpaRepository maturityLevelRepository;
    private final QuestionnaireJpaRepository questionnaireRepository;

    @Override
    public void update(UpdateQuestionPort.Param param) {
        repository.update(param.id(),
            param.title(),
            param.index(),
            param.hint(),
            param.mayNotBeApplicable(),
            param.advisable(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }

    @Override
    public Long persist(CreateQuestionPort.Param param) {
        return repository.save(mapToJpaEntity(param)).getId();
    }

    @Override
    public int countBySubjectId(long subjectId) {
        return repository.countDistinctBySubjectId(subjectId);
    }


    @Override
    public Question load(long id, long kitId) {
        var questionEntity = repository.findByIdAndKitId(id, kitId)
            .orElseThrow(() -> new ResourceNotFoundException(QUESTION_ID_NOT_FOUND));
        Question question = QuestionMapper.mapToDomainModel(questionEntity);

        var impacts = questionImpactRepository.findAllByQuestionId(id).stream()
            .map(QuestionImpactMapper::mapToDomainModel)
            .map(this::setOptionImpacts)
            .toList();

        var options = answerOptionRepository.findByQuestionId(id).stream()
            .map(AnswerOptionMapper::mapToDomainModel)
            .toList();

        question.setImpacts(impacts);
        question.setOptions(options);
        return question;
    }

    private QuestionImpact setOptionImpacts(QuestionImpact impact) {
        impact.setOptionImpacts(
            answerOptionImpactRepository.findAllByQuestionImpactId(impact.getId()).stream()
                .map(AnswerOptionImpactMapper::mapToDomainModel)
                .toList()
        );
        return impact;
    }
    @Override
    public Result loadAttrLevelQuestionsInfo(Long attributeId, Long maturityLevelId) {
        List<AttrLevelQuestionView> attrLevelQuestionViews = repository.findByAttributeIdAndMaturityLevelId(attributeId, maturityLevelId);
        MaturityLevelJpaEntity maturityLevelEntity = maturityLevelRepository.findById(maturityLevelId).get();
        List<Long> questionIds = attrLevelQuestionViews.stream()
            .map(AttrLevelQuestionView::getId)
            .toList();

        Set<Long> questionnaireIds = attrLevelQuestionViews.stream()
            .map(AttrLevelQuestionView::getQuestionnaireId)
            .collect(Collectors.toSet());

        List<QuestionnaireTitleView> questionnaireTitleViews = questionnaireRepository.findAllTitlesById(questionnaireIds);
        Map<Long, String> questionnaireIdToTitle = questionnaireTitleViews.stream()
            .collect(Collectors.toMap(QuestionnaireTitleView::getId, QuestionnaireTitleView::getTitle));

        var impactfulAnswerOptionViews = answerOptionRepository.findAllByAttrIdAndMaturityLevelIdAndQuestionIdIn(
            attributeId,
            maturityLevelId,
            questionIds);

        Map<Long, List<ImpactfulAnswerOptionView>> questionIdToImpactfulAnswerOptionViews = impactfulAnswerOptionViews.stream()
            .collect(Collectors.groupingBy(ImpactfulAnswerOptionView::getQuestionId));

        questionIdToImpactfulAnswerOptionViews.forEach((k, v) -> v.sort(Comparator.comparing(ImpactfulAnswerOptionView::getIndex)));

        List<Result.Question> questions = attrLevelQuestionViews.stream()
            .map(e -> {
                List<ImpactfulAnswerOptionView> views = questionIdToImpactfulAnswerOptionViews.get(e.getId());
                List<Result.Question.AnswerOption> options = views.stream()
                    .map(x -> new Result.Question.AnswerOption(x.getIndex(), x.getTitle(), x.getImpactValue()))
                    .toList();

                return new Result.Question(e.getIndex(),
                    e.getTitle(),
                    e.getMayNotBeApplicable(),
                    e.getWeight(),
                    questionnaireIdToTitle.get(e.getQuestionnaireId()),
                    options);
            }).toList();

        return new Result(maturityLevelId,
            maturityLevelEntity.getTitle(),
            maturityLevelEntity.getIndex(),
            questions.size(),
            questions);
    }
}
