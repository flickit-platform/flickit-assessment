package org.flickit.assessment.kit.application.service.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.port.in.question.GetKitQuestionDetailUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadActiveKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAllAttributesPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitQuestionDetailService implements GetKitQuestionDetailUseCase {

    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadAllAttributesPort loadAllAttributesPort;
    private final LoadQuestionPort loadQuestionPort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final LoadActiveKitVersionIdPort loadActiveKitVersionIdPort;

    @Override
    public Result getKitQuestionDetail(Param param) {
        var expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId());
        if (!checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var kitVersionId = loadActiveKitVersionIdPort.loadKitVersionId(param.getKitId());

        Question question = loadQuestionPort.load(param.getQuestionId(), kitVersionId);

        var maturityLevelsMap = loadMaturityLevelsPort.loadAllByKitVersionId(kitVersionId).stream()
            .collect(toMap(MaturityLevel::getId, e -> e));
        var options = question.getOptions().stream()
            .map(opt -> new Option(opt.getIndex(), opt.getTitle()))
            .sorted(comparingInt(Option::index))
            .toList();

        List<Impact> attributeImpacts = loadAttributeImpacts(kitVersionId, question, maturityLevelsMap);

        return new Result(question.getHint(), options, attributeImpacts);
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

    private AffectedLevel toAffectedLevel(Map<Long, Integer> answerIdToIndexMap, QuestionImpact attributeImpact,
                                          MaturityLevel maturityLevel) {
        List<AffectedLevel.OptionValue> optionValues = attributeImpact.getOptionImpacts().stream()
            .map(answer -> new AffectedLevel.OptionValue(answer.getOptionId(), answerIdToIndexMap.get(answer.getOptionId()), answer.getValue()))
            .toList();

        return new AffectedLevel(
            new AffectedLevel.MaturityLevel(maturityLevel.getId(), maturityLevel.getIndex(), maturityLevel.getTitle()),
            attributeImpact.getWeight(),
            optionValues
        );
    }
}
