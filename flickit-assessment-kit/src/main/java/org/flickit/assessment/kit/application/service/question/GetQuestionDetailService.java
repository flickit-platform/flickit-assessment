package org.flickit.assessment.kit.application.service.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.domain.AnswerOptionImpact;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.port.in.question.GetQuestionDetailUseCase;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionsByQuestionAndKitPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAllAttributesPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.LoadQuestionImpactByQuestionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.LoadQuestionImpactByQuestionPort.AttributeImpact;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_QUESTION_DETAIL_QUESTION_ID_NOT_FOUND;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetQuestionDetailService implements GetQuestionDetailUseCase {

    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadAnswerOptionsByQuestionAndKitPort loadAnswerOptionsByQuestionPort;
    private final LoadQuestionImpactByQuestionPort loadQuestionImpactByQuestionPort;
    private final LoadAllAttributesPort loadAllAttributesPort;

    @Override
    public Result getQuestionDetail(Param param) {
        var expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId());
        if (!checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var answerOptions = loadAnswerOptionsByQuestionPort.loadByQuestionIdAndKitId(param.getQuestionId(), param.getKitId());
        if (answerOptions.isEmpty()) {
            throw new ResourceNotFoundException(GET_QUESTION_DETAIL_QUESTION_ID_NOT_FOUND);
        }
        var options = answerOptions.stream().map(this::toOption)
            .toList();

        List<Impact> attributeImpacts = loadAttributeImpacts(param, answerOptions);
        return new Result(options, attributeImpacts);
    }

    private Option toOption(AnswerOption answerOption) {
        return new Option(answerOption.getIndex(), answerOption.getTitle());
    }

    private List<Impact> loadAttributeImpacts(Param param, List<AnswerOption> answerOptions) {
        var loadedAttributeImpacts = loadQuestionImpactByQuestionPort.loadQuestionImpactByQuestionId(param.getQuestionId());

        var answerIdToIndexMap = answerOptions.stream()
            .collect(toMap(AnswerOption::getId, AnswerOption::getIndex));
        var attributeIds = loadedAttributeImpacts.stream()
            .map(AttributeImpact::attributeId)
            .toList();
        var attributeIdToTitleMap = loadAllAttributesPort.loadAllByIds(attributeIds).stream()
            .collect(toMap(Attribute::getId, Attribute::getTitle));
        return loadedAttributeImpacts.stream()
            .map(attributeImpact -> toAttributeImpact(answerIdToIndexMap, attributeIdToTitleMap, attributeImpact))
            .toList();
    }

    private Impact toAttributeImpact(Map<Long, Integer> answerIdToIndexMap, Map<Long, String> attributeIdToTitleMap, AttributeImpact attributeImpact) {
        var attributeId = attributeImpact.attributeId();
        var title = attributeIdToTitleMap.get(attributeId);
        var affectedLevels = attributeImpact.affectedLevels().stream()
            .map(affectedLevel -> toAffectedLevel(answerIdToIndexMap, affectedLevel))
            .toList();
        return new Impact(attributeId,
            title,
            affectedLevels
        );
    }

    private AffectedLevel toAffectedLevel(Map<Long, Integer> answerIdToIndexMap, LoadQuestionImpactByQuestionPort.AffectedLevel affectedLevel) {
        var maturityLevel = affectedLevel.maturityLevel();
        List<OptionValue> optionValues = affectedLevel.optionValues().stream()
            .map(answer -> toOptionValue(answerIdToIndexMap, answer))
            .toList();

        return new AffectedLevel(
            new MaturityLevel(maturityLevel.getId(), maturityLevel.getTitle(), maturityLevel.getIndex()),
            affectedLevel.weight(),
            optionValues
        );
    }

    private OptionValue toOptionValue(Map<Long, Integer> answerIdToIndexMap, AnswerOptionImpact answer) {
        var id = answer.getOptionId();
        return new OptionValue(id, answerIdToIndexMap.get(id), answer.getValue());
    }
}
