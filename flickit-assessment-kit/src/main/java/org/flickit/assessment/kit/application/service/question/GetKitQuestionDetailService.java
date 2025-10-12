package org.flickit.assessment.kit.application.service.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.port.in.question.GetKitQuestionDetailUseCase;
import org.flickit.assessment.kit.application.port.out.answerrange.LoadAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadActiveKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.kit.application.port.out.measure.LoadMeasurePort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.MEASURE_ID_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitQuestionDetailService implements GetKitQuestionDetailUseCase {

    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadAttributesPort loadAttributesPort;
    private final LoadQuestionPort loadQuestionPort;
    private final LoadMaturityLevelsPort loadMaturityLevelsPort;
    private final LoadActiveKitVersionIdPort loadActiveKitVersionIdPort;
    private final LoadAnswerRangePort loadAnswerRangePort;
    private final LoadMeasurePort loadMeasurePort;

    @Override
    public Result getKitQuestionDetail(Param param) {
        var expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId());
        if (!checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var kitVersionId = loadActiveKitVersionIdPort.loadKitVersionId(param.getKitId());

        Question question = loadQuestionPort.load(param.getQuestionId(), kitVersionId);

        var maturityLevelsMap = loadMaturityLevelsPort.loadAllByKitVersionId(kitVersionId).stream()
            .collect(toMap(MaturityLevel::getId, e -> e));

        List<Impact> attributeImpacts = loadAttributeImpacts(kitVersionId, question, maturityLevelsMap);
        var answerRange = loadAnswerRangePort.load(question.getAnswerRangeId(), kitVersionId);

        var measure = loadMeasurePort.load(question.getMeasureId(), kitVersionId)
            .orElseThrow(() -> new ResourceNotFoundException(MEASURE_ID_NOT_FOUND)); //Can't happen

        return buildResult(answerRange, question, attributeImpacts, measure);
    }

    private List<Impact> loadAttributeImpacts(long kitVersionId, Question question, Map<Long, MaturityLevel> maturityLevelsMap) {
        var impacts = question.getImpacts();

        Map<Long, List<QuestionImpact>> attributeIdToImpacts = impacts.stream()
            .collect(groupingBy(QuestionImpact::getAttributeId,
                mapping(e -> e, toList())));

        var attributeIds = attributeIdToImpacts.keySet().stream().toList();

        var attributeIdToTitleMap = loadAttributesPort.loadAllByIdsAndKitVersionId(attributeIds, kitVersionId).stream()
            .collect(toMap(AttributeMini::getId, AttributeMini::getTitle));
        return attributeIds.stream()
            .map(attributeId -> toAttributeImpact(
                attributeId,
                attributeIdToTitleMap.get(attributeId),
                attributeIdToImpacts.get(attributeId),
                maturityLevelsMap))
            .toList();
    }

    private Impact toAttributeImpact(long attributeId, String attributeTitle,
                                     List<QuestionImpact> attributeImpacts, Map<Long, MaturityLevel> maturityLevelsMap) {
        var affectedLevels = attributeImpacts.stream()
            .map(impact -> toAffectedLevel(
                impact,
                maturityLevelsMap.get(impact.getMaturityLevelId())))
            .toList();
        return new Impact(attributeId,
            attributeTitle,
            affectedLevels
        );
    }

    private AffectedLevel toAffectedLevel(QuestionImpact attributeImpact, MaturityLevel maturityLevel) {
        return new AffectedLevel(
            new AffectedLevel.MaturityLevel(maturityLevel.getId(), maturityLevel.getIndex(), maturityLevel.getTitle()),
            attributeImpact.getWeight()
        );
    }

    private static Result buildResult(AnswerRange answerRange,
                                      Question question,
                                      List<Impact> attributeImpacts,
                                      Measure measure) {
        if (answerRange.isReusable()) {
            return new Result(
                question.getHint(),
                null,
                attributeImpacts,
                QuestionDetailAnswerRange.of(answerRange),
                QuestionDetailMeasure.of(measure),
                question.getTranslations()
            );
        }

        List<Option> options = question.getOptions().stream()
            .map(opt -> new Option(opt.getIndex(), opt.getTitle(), opt.getValue(), opt.getTranslations()))
            .toList();

        return new Result(
            question.getHint(),
            options,
            attributeImpacts,
            null,
            QuestionDetailMeasure.of(measure),
            question.getTranslations()
        );
    }
}
