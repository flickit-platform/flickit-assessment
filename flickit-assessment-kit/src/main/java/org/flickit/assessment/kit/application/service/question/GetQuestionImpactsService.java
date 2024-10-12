package org.flickit.assessment.kit.application.service.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.port.in.question.GetQuestionImpactsUseCase;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAllAttributesPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toList;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetQuestionImpactsService implements GetQuestionImpactsUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadQuestionPort loadQuestionPort;
    private  final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final LoadAllAttributesPort loadAllAttributesPort;

    @Override
    public Result getQuestionImpacts(Param param) {
        var kitVersionId = loadKitVersionPort.load(param.getKitVersionId());
        if (!checkExpertGroupAccessPort.checkIsMember(kitVersionId.getKit().getExpertGroupId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        Question question = loadQuestionPort.load(param.getQuestionId(), param.getKitVersionId());

        var maturityLevelsMap = loadMaturityLevelsPort.loadAllByKitVersionId(param.getKitVersionId()).stream()
            .collect(toMap(MaturityLevel::getId, e -> e));

        var x = loadAttributeImpacts(param.getKitVersionId(), question, maturityLevelsMap);

        return new Result(param.getQuestionId(), x);
    }

    private List<Impact> loadAttributeImpacts(long kitVersionId, Question question, Map<Long, MaturityLevel> maturityLevelsMap) {
        var impacts = question.getImpacts();

        var answerIdToIndexMap = question.getOptions().stream()
            .collect(toMap(AnswerOption::getId, AnswerOption::getIndex));

        Map<Long, List<QuestionImpact>> attributeIdToImpacts = impacts.stream()
            .collect(groupingBy(QuestionImpact::getAttributeId,
                mapping(e -> e, toList())));

        var attributeIds = attributeIdToImpacts.keySet().stream().toList();

        var attributeIdToTitleMap = loadAllAttributesPort.loadAllByIdsAndKitVersionId(attributeIds, kitVersionId).stream()
            .collect(toMap(Attribute::getId, Attribute::getTitle));
        return attributeIds.stream()
            .map(attributeId -> toAttributeImpact(
                attributeId,
                answerIdToIndexMap,
                attributeIdToTitleMap.get(attributeId),
                attributeIdToImpacts.get(attributeId),
                maturityLevelsMap))
            .toList();
    }

    private Impact toAttributeImpact(long attributeId, Map<Long, Integer> answerIdToIndexMap, String attributeTitle,
                                                                 List<QuestionImpact> attributeImpacts, Map<Long, MaturityLevel> maturityLevelsMap) {
        var affectedLevels = attributeImpacts.stream()
            .map(impact -> toAffectedLevel(
                answerIdToIndexMap,
                impact,
                maturityLevelsMap.get(impact.getMaturityLevelId())))
            .toList();
        return new Impact(attributeId,
            attributeTitle,
            affectedLevels
        );
    }

    private ImpactLevel toAffectedLevel(Map<Long, Integer> answerIdToIndexMap, QuestionImpact attributeImpact,
                                                                      MaturityLevel maturityLevel) {
        List<ImpactLevel.OptionValue> optionValues = attributeImpact.getOptionImpacts().stream()
            .map(answer -> new ImpactLevel.OptionValue(answer.getOptionId(), answer.getValue()))
            .toList();

        return new ImpactLevel(
            attributeImpact.getId(),
            attributeImpact.getWeight(),
            new ImpactLevel.MaturityLevel(maturityLevel.getId(), maturityLevel.getTitle()),
            optionValues
        );
    }
}
