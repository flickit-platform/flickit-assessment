package org.flickit.assessment.kit.application.service.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.port.in.questionimpact.GetQuestionImpactsUseCase;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionPort;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetQuestionImpactsService implements GetQuestionImpactsUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadQuestionPort loadQuestionPort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final LoadAttributesPort loadAttributesPort;

    @Override
    public Result getQuestionImpacts(Param param) {
        var kitVersionId = loadKitVersionPort.load(param.getKitVersionId());
        if (!checkExpertGroupAccessPort.checkIsMember(kitVersionId.getKit().getExpertGroupId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        Question question = loadQuestionPort.load(param.getQuestionId(), param.getKitVersionId());

        if (question.getImpacts() == null)
            return new Result(List.of());

        var maturityLevelsMap = loadMaturityLevelsPort.loadAllByKitVersionId(param.getKitVersionId()).stream()
            .collect(toMap(MaturityLevel::getId, e -> e));

        var attributeImpacts = loadAttributeImpacts(param.getKitVersionId(), question, maturityLevelsMap);

        return new Result(attributeImpacts);
    }

    private List<AttributeImpact> loadAttributeImpacts(long kitVersionId, Question question, Map<Long, MaturityLevel> maturityLevelsMap) {
        var impacts = question.getImpacts();

        Map<Long, List<QuestionImpact>> attributeIdToImpacts = impacts.stream()
            .collect(groupingBy(QuestionImpact::getAttributeId,
                mapping(e -> e, toList())));

        var attributeIds = attributeIdToImpacts.keySet().stream().toList();

        var language = KitLanguage.valueOf(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        var attributeIdToTitleMap = loadAttributesPort.loadAllByIdsAndKitVersionId(attributeIds, kitVersionId, language).stream()
            .collect(toMap(AttributeMini::getId, AttributeMini::getTitle));
        return attributeIds.stream()
            .map(attributeId -> toAttributeImpact(
                attributeId,
                attributeIdToTitleMap.get(attributeId),
                attributeIdToImpacts.get(attributeId),
                maturityLevelsMap))
            .toList();
    }

    private AttributeImpact toAttributeImpact(long attributeId, String attributeTitle,
                                              List<QuestionImpact> attributeImpacts, Map<Long, MaturityLevel> maturityLevelsMap) {
        var impacts = attributeImpacts.stream()
            .map(impact -> toImpact(
                impact,
                maturityLevelsMap.get(impact.getMaturityLevelId())))
            .toList();
        return new AttributeImpact(attributeId, attributeTitle, impacts);
    }

    private Impact toImpact(QuestionImpact attributeImpact, MaturityLevel maturityLevel) {
        return new Impact(
            attributeImpact.getId(),
            attributeImpact.getWeight(),
            new Impact.MaturityLevel(maturityLevel.getId(), maturityLevel.getTitle())
        );
    }
}
